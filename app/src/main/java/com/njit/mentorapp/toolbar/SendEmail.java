package com.njit.mentorapp.toolbar;

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

import com.njit.mentorapp.R;
import com.njit.mentorapp.model.users.User;

public class SendEmail extends AppCompatActivity {

    EditText TO, SUBJECT, BODY;
    ImageView send;
    SharedPreferences USER_TYPE;
    private User user;
    boolean done = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_email);
        TO = findViewById(R.id.to_email);
        SUBJECT = findViewById(R.id.email_subject);
        BODY = findViewById(R.id.email_body);
        send = findViewById(R.id.sendEmail);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayOptions(android.support.v7.app.ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle("Send Email");
        USER_TYPE = getSharedPreferences("USER_TYPE", Context.MODE_PRIVATE);

        if(isStudent(USER_TYPE))
            user = new User(getApplicationContext(), "Mentee");
        else
            user = new User(getApplicationContext(), "Mentor");
    }

    /* After onCreateView(), collect the form data and send an intent to their preferred email
    * application to finish sending the email message. */
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
    }

    /* After completing the email process, resume here and send you back to your previous
    * opened page */
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
        intent.setData(Uri.parse("mailto:" + address)); // only email apps should handle this
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

    /* Define who the user type is to pass their email address in the Email API */
    private boolean isStudent(SharedPreferences type) {
        if(type.getString("type", null).equals("student"))
        {
            return true;
        }
        else{
            return false;
        }
    }
}
