package com.eswaraj.app.eswaraj.fragments;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
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
import com.eswaraj.app.eswaraj.base.BaseFragment;
import com.eswaraj.app.eswaraj.events.GetUserEvent;
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

    private Button takePhoto;
    private Button attachPhoto;
    private Button post;
    private EditText description;
    private TextView selected;

    private CategoryWithChildCategoryDto categoryWithChildCategoryDto;
    private UserDto userDto;

    private static final int SELECT_PHOTO = 1;
    private static final int TAKE_PHOTO = 2;

    private File photoFile;
    private File selectedFile;

    public AddDetailsFragment() {
        selectedFile = null;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_add_details, container, false);

        //Get handles
        takePhoto = (Button) rootView.findViewById(R.id.adTakePhoto);
        attachPhoto = (Button) rootView.findViewById(R.id.adAttachPhoto);
        post = (Button) rootView.findViewById(R.id.adPost);
        description = (EditText) rootView.findViewById(R.id.adDescription);
        selected = (TextView) rootView.findViewById(R.id.adSelected);

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
                //TODO: Pick selectedFile, data from Description field and rest attributes from UserDto and post
            }
        });
        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SELECT_PHOTO:
                if(resultCode == Activity.RESULT_OK) {
                    //Log.d("AttachPhoto", getPath(getActivity(), data.getData()));
                    selectedFile = new File(data.getData().getPath());
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

    public void onEventMainThread(GetUserEvent event) {
        if(event.getSuccess()) {
            Log.d("AddDetailsFragment", "Got UserDto");
            userDto = event.getUserDto();
        }
        else {
            //This will never happen because user cant reach this screen unless UserDto was received successfully on Splash Screen
        }
    }
}
