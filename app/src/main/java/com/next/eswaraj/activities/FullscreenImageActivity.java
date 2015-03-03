package com.next.eswaraj.activities;

import android.graphics.Bitmap;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.next.eswaraj.R;
import com.next.eswaraj.base.BaseFragmentActivity;
import com.squareup.picasso.Picasso;

import it.sephiroth.android.library.imagezoom.ImageViewTouch;
import it.sephiroth.android.library.imagezoom.ImageViewTouchBase;

public class FullscreenImageActivity extends BaseFragmentActivity {

    private ImageViewTouch fiImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen_image);
        fiImage = (ImageViewTouch) findViewById(R.id.fiImage);
        fiImage.setDisplayType(ImageViewTouchBase.DisplayType.FIT_TO_SCREEN);
        Picasso.with(this).load(getIntent().getStringExtra("IMAGE")).into(fiImage);
    }

}
