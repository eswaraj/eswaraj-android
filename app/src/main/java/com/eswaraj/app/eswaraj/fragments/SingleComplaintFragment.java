package com.eswaraj.app.eswaraj.fragments;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.eswaraj.app.eswaraj.R;
import com.eswaraj.app.eswaraj.base.BaseFragment;
import com.eswaraj.app.eswaraj.events.GetCategoriesDataEvent;
import com.eswaraj.web.dto.CategoryWithChildCategoryDto;
import com.eswaraj.web.dto.ComplaintDto;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;

import java.util.List;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;


public class SingleComplaintFragment extends BaseFragment implements OnMapReadyCallback {

    @Inject
    EventBus eventBus;

    private CommentsFragment commentsFragment;
    private ImageFragment imageFragment;
    private GoogleMapFragment googleMapFragment;
    private ComplaintDto complaintDto;
    private List<CategoryWithChildCategoryDto> categoryList;
    private Boolean imageSelected = true;

    private Button scPhoto;
    private Button scMap;
    private TextView scCategory;
    private TextView scDescription;

    public SingleComplaintFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_single_complaint, container, false);

        //Get handles
        scPhoto = (Button) rootView.findViewById(R.id.scPhoto);
        scMap = (Button) rootView.findViewById(R.id.scMap);
        scCategory = (TextView) rootView.findViewById(R.id.scCategory);
        scDescription = (TextView) rootView.findViewById(R.id.scDescription);

        //Create fragments
        commentsFragment = new CommentsFragment();
        imageFragment = new ImageFragment();
        googleMapFragment = new GoogleMapFragment();

        //Get data from intent
        complaintDto = (ComplaintDto) getActivity().getIntent().getSerializableExtra("COMPLAINT");


        scDescription.setText(complaintDto.getDescription());

        //Set up fragments
        commentsFragment.setComplaintDto(complaintDto);
        if(complaintDto.getImages() != null) {
            imageFragment.setImage(complaintDto.getImages().get(0).getOrgUrl(), complaintDto.getId());
        }
        googleMapFragment.setContext(this);

        //Add fragments
        getActivity().getSupportFragmentManager().beginTransaction().add(R.id.scCommentContainer, commentsFragment).commit();
        if(complaintDto.getImages() != null) {
            getActivity().getSupportFragmentManager().beginTransaction().add(R.id.scDisplayContainer, imageFragment).commit();
        }
        else {
            getActivity().getSupportFragmentManager().beginTransaction().add(R.id.scDisplayContainer, googleMapFragment).commit();
            scPhoto.setVisibility(View.INVISIBLE);
            scMap.setVisibility(View.INVISIBLE);
        }

        //Register listeners
        scPhoto.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!imageSelected) {
                    imageSelected = true;
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.scDisplayContainer, imageFragment).commit();
                }
            }
        });
        scMap.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(imageSelected) {
                    imageSelected = false;
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.scDisplayContainer, googleMapFragment).commit();
                }
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

    public void onEventMainThread(GetCategoriesDataEvent event) {
        if(event.getSuccess()) {
            categoryList = event.getCategoryList();
            //Update text
            for(CategoryWithChildCategoryDto root : categoryList) {
                if(root.getChildCategories() != null) {
                    for (CategoryWithChildCategoryDto child : root.getChildCategories()) {
                        if (child.getId() == complaintDto.getCategoryId()) {
                            scCategory.setText(child.getName());
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMapFragment.disableGestures();
        googleMapFragment.updateMarkerLocation(complaintDto.getLattitude(), complaintDto.getLongitude());
    }
}
