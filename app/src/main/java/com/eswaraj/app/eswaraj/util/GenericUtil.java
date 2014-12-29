package com.eswaraj.app.eswaraj.util;


import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.CursorLoader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;

public class GenericUtil {

    public final static double AVERAGE_RADIUS_OF_EARTH = 6371;

    public static double calculateDistance(double userLat, double userLng,
                                 double venueLat, double venueLng) {

        double latDistance = Math.toRadians(userLat - venueLat);
        double lngDistance = Math.toRadians(userLng - venueLng);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(userLat)) * Math.cos(Math.toRadians(venueLat))
                * Math.sin(lngDistance / 2) * Math.sin(lngDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return AVERAGE_RADIUS_OF_EARTH * c * 1000;
    }

    public static String getPathFromUri(Context context, Uri uri) {
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

    public static String getUniqueImageFilename() {
        return String.valueOf(Calendar.getInstance().getTimeInMillis());
    }

    public static String getRealPathFromURI(Context context, Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        try {
            CursorLoader loader = new CursorLoader(context, contentUri, proj, null, null, null);
            Cursor cursor = loader.loadInBackground();
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
            if (contentUri.toString().startsWith("content://com.google.android.gallery3d")){
                return getImage(context, getUniqueImageFilename(), contentUri);
            } else { // it is a regular local image file
                return cursor.getString(columnIndex);
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        String path = contentUri.getPath();
        return path;
    }

    private static String getImage(Context context, String tag, Uri url) {
        File f = createImageFileInCache(context, tag);
        try {
            InputStream imageStream = null;
            imageStream = context.getContentResolver().openInputStream(url);
            OutputStream out = new FileOutputStream(f);
            byte buf[] = new byte[1024];
            int len;
            while ((len = imageStream.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            out.close();
            imageStream.close();
            return f.getPath();
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private static File createImageFileInCache(Context context, String tag) {
        File cacheDir;
        if (isExternalStorageUsable()) {
            cacheDir = context.getExternalCacheDir();
        } else {
            cacheDir = context.getCacheDir();
        }
        if (!cacheDir.exists())
            cacheDir.mkdirs();

        File f = new File(cacheDir, tag);
        return f;
    }

    public static boolean isExternalStorageUsable() {
        boolean isExternalStorageAvailable = false;
        boolean isExternalStorageWriteable = false;
        String state = Environment.getExternalStorageState();

        if (Environment.MEDIA_MOUNTED.equals(state)) {
            // We can read and write the media
            isExternalStorageAvailable = isExternalStorageWriteable = true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            // We can only read the media
            isExternalStorageAvailable = true;
            isExternalStorageWriteable = false;
        } else {
            // Something else is wrong. It may be one of many other states, but all we need
            //  to know is we can neither read nor write
            isExternalStorageAvailable = isExternalStorageWriteable = false;
        }

        return isExternalStorageAvailable && isExternalStorageWriteable;
    }
}
