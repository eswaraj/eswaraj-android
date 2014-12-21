package com.eswaraj.app.eswaraj.fragments;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.eswaraj.app.eswaraj.R;
import com.eswaraj.app.eswaraj.activities.SelectAmenityActivity;
import com.eswaraj.app.eswaraj.base.BaseFragment;
import com.eswaraj.app.eswaraj.events.GetCategoriesDataEvent;
import com.eswaraj.app.eswaraj.events.SavedComplaintEvent;
import com.eswaraj.web.dto.CategoryWithChildCategoryDto;
import com.eswaraj.web.dto.ComplaintDto;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import java.io.File;
import java.util.List;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;


public class ComplaintSummaryFragment extends BaseFragment implements OnMapReadyCallback {

    @Inject
    EventBus eventBus;

    private GoogleMapFragment googleMapFragment;
    private File imageFile;
    private ComplaintDto complaintDto;
    private List<CategoryWithChildCategoryDto> categoryList;

    private TextView mlaName;
    private ImageView mlaPhoto;
    private TextView category;
    private ImageView complaintPhoto;
    private Button done;

    public ComplaintSummaryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_complaint_summary, container, false);
        mlaName = (TextView) rootView.findViewById(R.id.csMlaName);
        mlaPhoto = (ImageView) rootView.findViewById(R.id.csMlaPhoto);
        category = (TextView) rootView.findViewById(R.id.csCategory);
        complaintPhoto = (ImageView) rootView.findViewById(R.id.csComplaintPhoto);
        done = (Button) rootView.findViewById(R.id.csDone);

        googleMapFragment = new GoogleMapFragment();
        getActivity().getSupportFragmentManager().beginTransaction().add(R.id.csMapContainer, googleMapFragment).commit();
        googleMapFragment.setContext(this);

        imageFile = (File) getActivity().getIntent().getSerializableExtra("IMAGE");
        complaintDto = (ComplaintDto) getActivity().getIntent().getSerializableExtra("COMPLAINT");

        if(imageFile != null) {
            Bitmap myBitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
            complaintPhoto.setImageBitmap(myBitmap);
        }

        done.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), SelectAmenityActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                getActivity().startActivity(i);
            }

        });
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        eventBus.registerSticky(this);
    }

    @Override
    public void onStop() {
        eventBus.unregister(this);
        super.onStop();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMapFragment.disableGestures();
        googleMapFragment.updateMarkerLocation(complaintDto.getLattitude(), complaintDto.getLongitude());
    }


    public void onEventMainThread(GetCategoriesDataEvent event) {
        if(event.getSuccess()) {
            categoryList = event.getCategoryList();
            for(CategoryWithChildCategoryDto root : categoryList) {
                if(root.getChildCategories() != null) {
                    for (CategoryWithChildCategoryDto child : root.getChildCategories()) {
                        if (child.getId() == complaintDto.getCategoryId()) {
                            category.setText(child.getName());
                            break;
                        }
                    }
                }
            }
        }
        else {
            //This will never happen
        }
    }
}
