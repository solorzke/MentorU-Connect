package com.example.mentorapp.Events;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.EditText;

import com.example.mentorapp.R;

public class RequestMeeting extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_meeting);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayOptions(android.support.v7.app.ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

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

    private boolean checkForm(EditText[] event){

        String title = event[0].getText().toString();
        String loc = event[1].getText().toString();
        String dt = event[2].getText().toString();
        String st = event[3].getText().toString();
        String et = event[4].getText().toString();
        String purpose = event[5].getText().toString();

        if(title.equals("") || loc.equals("") || dt.equals("") || st.equals("") || et.equals("")
                || purpose.equals("")){
            return false;
        }
        return true;
    }
}
