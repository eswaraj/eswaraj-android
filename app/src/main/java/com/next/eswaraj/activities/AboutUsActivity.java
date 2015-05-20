package com.next.eswaraj.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.next.eswaraj.R;
import com.next.eswaraj.base.BaseActivity;


public class AboutUsActivity extends BaseActivity implements View.OnClickListener {

    ImageView auPhone;
    ImageView auEmail;
    ImageView auWeb;
    ImageView auLike;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        auPhone = (ImageView) findViewById(R.id.auPhone);
        auEmail = (ImageView) findViewById(R.id.auEmail);
        auWeb = (ImageView) findViewById(R.id.auWeb);
        auLike = (ImageView) findViewById(R.id.auLike);

        auPhone.setOnClickListener(this);
        auEmail.setOnClickListener(this);
        auWeb.setOnClickListener(this);
        auLike.setOnClickListener(this);
    }

    public void onClick (View v){
        switch(v.getId()){
            case R.id.auPhone:
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse(getResources().getString(R.string.ourContactNumber)));
                startActivity(intent);
                break;
            case R.id.auEmail:
                String emailAddress[] = {getResources().getString(R.string.eswarajEmailID)};
                String message = getResources().getString(R.string.feedback);
                Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
                emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, emailAddress);
                emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getResources().getString(R.string.myFeedbackLabel));
                emailIntent.setType("plain/text");
                emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, message);
                startActivity(emailIntent);
                break;
            case R.id.auWeb:
                Intent browserIntent1 = new Intent(Intent.ACTION_VIEW, Uri.parse(getResources().getString(R.string.webSiteLink)));
                startActivity(browserIntent1);
                break;
            case R.id.auLike:
                Intent browserIntent2 = new Intent(Intent.ACTION_VIEW, Uri.parse(getResources().getString(R.string.playStoreLink)));
                startActivity(browserIntent2);
                break;

        }
    }
}
