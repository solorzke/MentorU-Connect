package com.example.mentorapp.CoachingLog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import com.example.mentorapp.R;
import com.example.mentorapp.model.DateTimeFormat;
import java.util.ArrayList;

public class Meeting extends AppCompatActivity
{
    private ArrayList<String> meeting = new ArrayList<>();
    TextView users;
    EditText title, location, date, s_time, e_time, purpose;
    /* array = ['id', 'sender', 'receiver', 'title', 'e_date', 'start_time', 'end_time',
                    'location', 'purpose', 'status'] */

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.meeting);
        meeting = getIntent().getExtras().getStringArrayList("meeting_details");
        title = findViewById(R.id.event_title);
        location = findViewById(R.id.event_location);
        purpose = findViewById(R.id.event_purpose);
        date = findViewById(R.id.event_date);
        s_time = findViewById(R.id.event_start_time);
        e_time = findViewById(R.id.event_end_time);
        users = findViewById(R.id.party);
        /* Set the toolbar */
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayOptions(android.support.v7.app.ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle("Meeting Details");
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        users.setText(meeting.get(1) + " & " + meeting.get(2));
        title.setText(meeting.get(3));
        location.setText(meeting.get(7));
        purpose.setText(meeting.get(8));
        date.setText(DateTimeFormat.formatDate(meeting.get(4)));
        s_time.setText(DateTimeFormat.format12HourTimeAsString(meeting.get(5)));
        e_time.setText(DateTimeFormat.format12HourTimeAsString(meeting.get(6)));
    }

    /* When clicking the back button, go back to the last page. */
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                onBackPressed();
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
