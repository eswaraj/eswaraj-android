package com.next.eswaraj.activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.WebView;

import com.next.eswaraj.R;
import com.next.eswaraj.base.BaseActivity;

public class DocumentActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(getResources().getString(R.string.titleDocumentActivity));
        WebView mWebView=new WebView(this);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.loadUrl("https://docs.google.com/gview?embedded=true&url="+getIntent().getStringExtra("URL"));
        setContentView(mWebView);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return(true);
        }

        return(super.onOptionsItemSelected(item));
    }
}
