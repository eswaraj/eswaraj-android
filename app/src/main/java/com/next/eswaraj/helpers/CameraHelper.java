package com.next.eswaraj.helpers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import com.next.eswaraj.base.BaseFragment;
import com.next.eswaraj.interfaces.CameraHelperCallback;
import com.next.eswaraj.util.GenericUtil;

public class CameraHelper {


	public static abstract class CameraUtilFragment extends BaseFragment implements CameraHelperCallback {

	}


	public static final int PICTURE_REQUEST_CODE = 0x10;

	private Uri outputFileUri;
	private String imagePath;
    private File imageFile;
	private int overLayType;
	private boolean showGallery;
    private Uri tempImageFileUrl;


    public int getOverLayType() {
		return overLayType;
	}
	private static final String IMAGE_NAME_INSTANCE = "image_name";


	public void setOverLayType(int overLayType) {
		this.overLayType = overLayType;
	}

	private CameraUtilFragment fragment;

    public CameraHelper() {

    }

	public CameraHelper(CameraUtilFragment fragment) {
		this.fragment = fragment;
		setShowGallery(true);
	}

    public void setFragment(CameraUtilFragment fragment) {
        this.fragment = fragment;
        setShowGallery(true);
    }


	public void setImageName(String imageName) {
		this.imagePath = imageName;
	}

	public Uri getOutputFileUri() {
		return outputFileUri;
	}

	public String getImageName() {
		return imagePath;
	}

    public File getImageFile() {
        return imageFile;
    }

    public  void openImageIntent( ) {
        setOutputImageUri();
        // Camera.
        final List<Intent> cameraIntents = new ArrayList<Intent>();
        final Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        final PackageManager packageManager = fragment.getActivity().getPackageManager();
        final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam) {
            final String packageName = res.activityInfo.packageName;
            final Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(packageName);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, tempImageFileUrl);
            cameraIntents.add(intent);
        }
        // Filesystem.
        final Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        
        Intent chooserIntent =  Intent.createChooser(galleryIntent, "Select Source");
        
        
        // Add the camera options.
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[] {}));
        
        fragment.getActivity().startActivityForResult(chooserIntent, PICTURE_REQUEST_CODE);
        
        
    }
    
    public void openOnlyGalleryIntent() {
    	setOutputImageUri();
    	 final Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
    	 fragment.getActivity().startActivityForResult(galleryIntent, PICTURE_REQUEST_CODE);
    }
    
    public void openOnlyCameraIntent() {
    	 setOutputImageUri();
         // Camera.
         final List<Intent> cameraIntents = new ArrayList<Intent>();
         final Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
         final PackageManager packageManager = fragment.getActivity().getPackageManager();
         final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
         for (ResolveInfo res : listCam) {
             final String packageName = res.activityInfo.packageName;
             final Intent intent = new Intent(captureIntent);
             intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
             intent.setPackage(packageName);
             intent.putExtra(MediaStore.EXTRA_OUTPUT, tempImageFileUrl);
             cameraIntents.add(intent);
         }
         Intent intent = cameraIntents.remove(cameraIntents.size() -1);
         Intent chooserIntent =  Intent.createChooser(intent, "Select Source");
         
         
         // Add the camera options.
         chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[] {}));
         
         fragment.getActivity().startActivityForResult(chooserIntent, PICTURE_REQUEST_CODE);
    }
    

    public  void setOutputImageUri() {
        // Determine Uri of camera image to save.
        File root;
        root = new File(Environment.getExternalStorageDirectory() + File.separator + "eSwaraj" + File.separator);
        root.mkdirs();

        final String name = GenericUtil.getUniqueImageFilename();
        final String fname = name + ".png";
        final File sdImageMainDirectory = new File(root, fname);
        imageFile = sdImageMainDirectory;
        imagePath = sdImageMainDirectory.getAbsolutePath();
        outputFileUri = Uri.fromFile(sdImageMainDirectory);
        
        final String tempName = name + "_temp.png" ;
        final File sdImageTemp = new File(root, tempName);
        tempImageFileUrl = Uri.fromFile(sdImageTemp);
    }
    
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    	if (resultCode == Activity.RESULT_OK) {
            if (requestCode == CameraHelper.PICTURE_REQUEST_CODE) {
                final boolean isCamera;
                if (data == null) {
                    isCamera = true;
                } else {
                    isCamera = MediaStore.ACTION_IMAGE_CAPTURE.equals(data.getAction());
                }

                Uri selectedImageUri;
                if (isCamera) {
                    selectedImageUri = tempImageFileUrl;
                    if(tempImageFileUrl == null) {
                        Log.e("CameraHelper", "URI is null");
                    }
                    else {
                        Log.e("CameraHelper", "URI is not null: " + tempImageFileUrl.toString());
                    }
                    compressAndSaveImage(GenericUtil.getRealPathFromURI(fragment.getActivity(), selectedImageUri), getOutputFileUri().getPath());
                    setImageName(getOutputFileUri().getPath());
                    onCameraPicTaken();
                } else {
                    selectedImageUri = data == null ? null : data.getData();  
                	compressAndSaveImage(GenericUtil.getRealPathFromURI(fragment.getActivity(), selectedImageUri), getOutputFileUri().getPath());
                    setImageName(getOutputFileUri().getPath());
                	if(TextUtils.isEmpty(getImageName())) {
                        Log.e("CameraHelper", "Empty Image Name");
                    } else {
                    	onGalleryPicChosen();
                    }
                }
            }
        }
    }
        
    private static void compressAndSaveImage(String selectedImage, String imagePath) {    	
    	OutputStream stream = null;
    	try {
    		stream = new FileOutputStream(imagePath);
    		Bitmap bmp = resizeBitMapImage1(selectedImage, 800, 600);      	
        	bmp.compress(CompressFormat.JPEG, 30, stream);
    	} catch (FileNotFoundException e) {
    	    e.printStackTrace();
    	}   	
    	try {
    	    stream.close();
    	    stream = null;
    	} catch (IOException e) {
    	    e.printStackTrace();
    	}
    }
    
    public static Bitmap resizeBitMapImage1(String filePath, int targetWidth, int targetHeight) {
        Bitmap bitMapImage = null;
        try {
            Options options = new Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(filePath, options);
            double sampleSize = 0;
            Boolean scaleByHeight = Math.abs(options.outHeight - targetHeight) >= Math.abs(options.outWidth
                    - targetWidth);
            if (options.outHeight * options.outWidth * 2 >= 1638) {
                sampleSize = scaleByHeight ? options.outHeight / targetHeight : options.outWidth / targetWidth;
                sampleSize = (int) Math.pow(2d, Math.floor(Math.log(sampleSize) / Math.log(2d)));
            }
            options.inJustDecodeBounds = false;
            options.inTempStorage = new byte[128];
            while (true) {
                try {
                    options.inSampleSize = (int) sampleSize;
                    bitMapImage = BitmapFactory.decodeFile(filePath, options);
                    break;
                } catch (Exception ex) {
                    try {
                        sampleSize = sampleSize * 2;
                    } catch (Exception ex1) {

                    }
                }
            }
        } catch (Exception ex) {

        }
        return bitMapImage;
    }
    
    
    public void onCameraPicTaken() {
    	fragment.onCameraPicTaken();
    }
    
    public void onGalleryPicChosen() {
    	Log.e("CameraHelper", "onGalleryPicChosen");
        fragment.onGalleryPicChosen();
    }
    	
	public void onSaveInstanceState(Bundle outState) {		
		outState.putString(IMAGE_NAME_INSTANCE, imagePath);
	}	

	public void onRestoreInstanceState(Bundle savedInstanceState) {
        if(savedInstanceState != null) {
            imagePath = savedInstanceState.getString(IMAGE_NAME_INSTANCE);
        }
	}

	public boolean isShowGallery() {
		return showGallery;
	}

	public void setShowGallery(boolean showGallery) {
		this.showGallery = showGallery;
	}
}
