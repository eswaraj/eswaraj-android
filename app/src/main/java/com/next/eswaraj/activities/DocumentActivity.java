package com.next.eswaraj.activities;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;

import com.next.eswaraj.R;
import com.next.eswaraj.base.BaseActivity;

public class DocumentActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_document);
        WebView mWebView=new WebView(this);
        mWebView.getSettings().setJavaScriptEnabled(true);
        //mWebView.getSettings().setPluginsEnabled(true);
        mWebView.loadUrl("https://docs.google.com/gview?embedded=true&url="+getIntent().getStringExtra("URL"));
        setContentView(mWebView);
    }


}
