package com.eswaraj.app.eswaraj.fragments;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.eswaraj.app.eswaraj.R;
import com.eswaraj.app.eswaraj.activities.ComplaintSummaryActivity;
import com.eswaraj.app.eswaraj.activities.LoginDialogActivity;
import com.eswaraj.app.eswaraj.base.BaseFragment;
import com.eswaraj.app.eswaraj.events.GetUserEvent;
import com.eswaraj.app.eswaraj.events.SavedComplaintEvent;
import com.eswaraj.app.eswaraj.middleware.MiddlewareService;
import com.eswaraj.app.eswaraj.middleware.MiddlewareServiceImpl;
import com.eswaraj.app.eswaraj.util.LocationUtil;
import com.eswaraj.app.eswaraj.util.UserSessionUtil;
import com.eswaraj.app.eswaraj.widgets.CustomProgressDialog;
import com.eswaraj.web.dto.CategoryWithChildCategoryDto;
import com.eswaraj.web.dto.UserDto;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;


public class AddDetailsFragment extends BaseFragment {

    @Inject
    EventBus eventBus;
    @Inject
    MiddlewareServiceImpl middlewareService;
    @Inject
    LocationUtil locationUtil;
    @Inject
    UserSessionUtil userSession;

    private Button takePhoto;
    private Button attachPhoto;
    private Button post;
    private Button DescriptionBtn;
    private EditText description;
    private TextView selected;

    private CategoryWithChildCategoryDto categoryWithChildCategoryDto;
    private Location location;

    private static final int SELECT_PHOTO = 1;
    private static final int TAKE_PHOTO = 2;

    private File photoFile;
    private File selectedFile = null;

    private Boolean posted = false;
    private CustomProgressDialog pDialog;

    public AddDetailsFragment() {
    }

    @Override
    public void onStart() {
        super.onStart();
        eventBus.registerSticky(this);
        locationUtil.startLocationService();
    }

    @Override
    public void onStop() {
        eventBus.unregister(this);
        locationUtil.stopLocationService();
        super.onStop();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_add_details, container, false);

        //Get handles
        takePhoto = (Button) rootView.findViewById(R.id.adTakePhoto);
        attachPhoto = (Button) rootView.findViewById(R.id.adAttachPhoto);
        post = (Button) rootView.findViewById(R.id.adPost);
        DescriptionBtn = (Button) rootView.findViewById(R.id.adDescriptionbtn);
        description = (EditText) rootView.findViewById(R.id.adDescription);
        selected = (TextView) rootView.findViewById(R.id.adSelected);

        //Init
        locationUtil.setup(getActivity());

        //Get the data from intent and display
        categoryWithChildCategoryDto = (CategoryWithChildCategoryDto) getActivity().getIntent().getSerializableExtra("TEMPLATE");
        selected.setText(categoryWithChildCategoryDto.getName());

        //Register on-click listeners
        takePhoto.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                // Ensure that there's a camera activity to handle the intent
                if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                    // Create the File where the photo should go
                    photoFile = null;
                    try {
                        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                        String imageFileName = "eSwaraj_" + timeStamp + "_";
                        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                        photoFile = File.createTempFile(
                                imageFileName,  /* prefix */
                                ".jpg",         /* suffix */
                                storageDir      /* directory */
                        );
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    // Continue only if the File was successfully created
                    if (photoFile != null) {
                        Log.d("TakePhoto", photoFile.getAbsolutePath());
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                        startActivityForResult(takePictureIntent, TAKE_PHOTO);
                    }
                }
            }
        });

        attachPhoto.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, SELECT_PHOTO);
            }
        });

        post.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(userSession.isUserLoggedIn(getActivity())) {
                    if (!posted) {
                        posted = true;
                        middlewareService.postComplaint(userSession.getUser(), categoryWithChildCategoryDto, location, description.getText().toString(), selectedFile);
                        pDialog = new CustomProgressDialog(getActivity(), false, true, "Posting your complaint ...");
                        pDialog.show();
                    }
                }
                else {
                    Intent i = new Intent(getActivity(), LoginDialogActivity.class);
                    startActivity(i);
                }
            }
        });
        setDescriptionText();
        setonLoadDescriptionBtn();
        return rootView;
    }

    public void setDescriptionText() {
        DescriptionBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                DescriptionBtn.setVisibility(View.INVISIBLE);
                description.requestFocus();
                description.setVisibility(View.VISIBLE);            }
        });
    }

    public void setonLoadDescriptionBtn() {
        DescriptionBtn.setVisibility(View.VISIBLE);
        description.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SELECT_PHOTO:
                if(resultCode == Activity.RESULT_OK) {
                    selectedFile = new File(getPath(getActivity(), data.getData()));
                }
                break;
            case TAKE_PHOTO:
                if(resultCode == Activity.RESULT_OK) {
                    selectedFile = photoFile;
                }
                else {
                    photoFile.delete();
                }
                break;
        }
    }

    public static String getPath(Context context, Uri uri) {
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = { "_data" };
            Cursor cursor = null;

            try {
                cursor = context.getContentResolver().query(uri, projection, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow("_data");
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    public void onEventMainThread(Location location) {
        this.location = location;
    }

    public void onEventMainThread(SavedComplaintEvent event) {
        pDialog.dismiss();
        if(event.getSuccess()) {
            Log.d("AddDetailsFragment", "Saved complaint");
            Intent i = new Intent(getActivity(), ComplaintSummaryActivity.class);
            i.putExtra("COMPLAINT", event.getComplaintDto());
            if(selectedFile != null) {
                Log.d("IMAGE", "Putting file: " + selectedFile.getAbsolutePath());
                i.putExtra("IMAGE", selectedFile);
            }
            getActivity().startActivity(i);
        }
        else {
            //If the request fails dont go to next screen instead try again
            Toast.makeText(getActivity(), "Complaint save failed. Try again. Error = " + event.getError(), Toast.LENGTH_LONG).show();
        }
    }
}
