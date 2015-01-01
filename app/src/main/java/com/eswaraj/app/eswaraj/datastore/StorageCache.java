package com.eswaraj.app.eswaraj.datastore;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.eswaraj.app.eswaraj.config.ImageType;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;

public class StorageCache {

    public void saveBitmap(Bitmap bitmap, Long id, Context context, ImageType type) {
        FileOutputStream fileOutput = null;
        try {
            String filename = "eSwaraj_" + type + "_" + id + ".png";
            File f = new File(context.getCacheDir(), filename);
            fileOutput = new FileOutputStream(f);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutput);
            // PNG is a lossless format, the compression factor (100) is ignored
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileOutput != null) {
                    fileOutput.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Bitmap getBitmap(Long id, Context context, ImageType type) {
        Bitmap bitmap = null;
        File f = new File(context.getCacheDir() + "/eSwaraj_" + type + "_" + id + ".png");
        if(f.exists()) {
            bitmap = BitmapFactory.decodeFile(f.getAbsolutePath());
        }
        return bitmap;
    }

    public synchronized void clear(Context context) {
        File[] files = context.getCacheDir().listFiles(new FilenameFilter()
        {
            public boolean accept(File dir, String name)
            {
                return name.endsWith(".png");
            }
        });
        for (File file : files) {
            file.delete();
        }
    }

    public Boolean isBitmapAvailable(Context context, Long id, ImageType type) {
        File f = new File(context.getCacheDir() + "/eSwaraj_" + type + "_" + id + ".png");
        if(f.exists()) {
            return true;
        }
        return false;
    }
}
