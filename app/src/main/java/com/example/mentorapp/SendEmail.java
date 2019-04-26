package com.example.mentorapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

public class SendEmail extends AppCompatActivity {

    EditText TO, SUBJECT, BODY;
    ImageView send, AC_IMG;
    SharedPreferences SESSION, RECIPIENT;
    boolean done = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_email);
        TO = findViewById(R.id.to_email);
        SUBJECT = findViewById(R.id.email_subject);
        BODY = findViewById(R.id.email_body);
        send = findViewById(R.id.sendEmail);
        AC_IMG = findViewById(R.id.ab_img);
        SESSION = getSharedPreferences("USER", Context.MODE_PRIVATE);
        RECIPIENT = getSharedPreferences("USER", Context.MODE_PRIVATE);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayOptions(android.support.v7.app.ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String r_email = TO.getText().toString();
                String sub = SUBJECT.getText().toString();
                String mess = BODY.getText().toString();
                composeEmail(r_email, sub, mess);
                done = true;
            }
        });
        /* Set Action Bar image to redirect user back to the home page */
        AC_IMG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SideBar.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(done){
            onBackPressed();
        }
    }

    /* Store email message info and pass over to EMAIL Application of their choice . */

    public void composeEmail(String address, String subject, String body) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:")); // only email apps should handle this
        intent.putExtra(Intent.EXTRA_EMAIL, address);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, body);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    /* When clicking the back button, go back to the last page. */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
