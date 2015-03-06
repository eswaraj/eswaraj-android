package com.next.eswaraj.activities;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.next.eswaraj.R;
import com.next.eswaraj.base.BaseActivity;


public class AboutUsActivity extends BaseActivity implements View.OnClickListener {

    ImageView iv1,iv2,iv3,iv4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        iv1 = (ImageView) findViewById(R.id.iv1);
        iv2 = (ImageView) findViewById(R.id.iv2);
        iv3 = (ImageView) findViewById(R.id.iv3);
        iv4 = (ImageView) findViewById(R.id.iv4);

        iv1.setOnClickListener(this);
        iv2.setOnClickListener(this);
        iv3.setOnClickListener(this);
        iv4.setOnClickListener(this);
    }

    public void onClick (View v){
        switch(v.getId()){
            case R.id.iv1:
                //Dialog D = new Dialog(this);
                //D.setTitle("Message from eSwaraj");
                //TextView tv = new TextView(this);
                //tv.setText("Please contact us at +91-9686");
                //D.setContentView(tv);
                //D.show();
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:09686860429"));
                startActivity(intent);
                break;
            case R.id.iv2:
                String emailAddress[] = { "eswaraj.india@gmail.com"};
                String message = "Feedback :\n";
                Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
                emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, emailAddress);
                emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "My Feedback for eSwaraj");
                emailIntent.setType("plain/text");
                emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, message);
                startActivity(emailIntent);
                break;
            case R.id.iv3:
                Intent browserIntent1 = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.eswaraj.com/"));
                startActivity(browserIntent1);
                break;
            case R.id.iv4:
                Intent browserIntent2 = new Intent(Intent.ACTION_VIEW, Uri.parse("http://bit.ly/1DS5Xn7"));
                startActivity(browserIntent2);
                break;

        }

    }

}
