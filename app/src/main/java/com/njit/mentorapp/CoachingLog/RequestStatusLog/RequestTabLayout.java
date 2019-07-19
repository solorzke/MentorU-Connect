package com.njit.mentorapp.CoachingLog.RequestStatusLog;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import com.ToxicBakery.viewpager.transforms.CubeOutTransformer;
import com.njit.mentorapp.R;
import com.njit.mentorapp.Model.Tools.DateTimeFormat;
import com.njit.mentorapp.Model.Tools.TabAdapter;
import java.util.ArrayList;
import java.util.Calendar;

public class RequestTabLayout extends AppCompatActivity
{
    private TabAdapter adapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private TextView addCalendar;
    private int[] tabIcons = {
            R.drawable.ic_check_red,
            R.drawable.ic_message_red,
    };
    ArrayList <String> row = new ArrayList<>();
    private String meeting_with;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.request_status);
        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);
        addCalendar = findViewById(R.id.addCalendar);
        adapter = new TabAdapter(getSupportFragmentManager());
        adapter.addFragment(new MeetingStatus(), "Status");
        adapter.addFragment(new MeetingDetails(), "Details");
        viewPager.setAdapter(adapter);
        viewPager.setPageTransformer(true, new CubeOutTransformer());
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        row = getIntent().getStringArrayListExtra("meeting_details");
        meeting_with = row.get(2);
        checkStatus();
        addCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createEvent(row);
            }
        });

        /* Set the toolbar */
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayOptions(android.support.v7.app.ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle("Request Details");
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

    private void checkStatus()
    {
        if(row.get(9).equals("1"))
            addCalendar.setVisibility(View.VISIBLE);
    }

    /* Function to create the Calendar event using its API. */
    private void createEvent(ArrayList <String> details)
    {
        /* array = ['id', 'sender', 'receiver', 'title', 'e_date', 'start_time', 'end_time',
                    'location', 'purpose', 'status'] */
        String formatted_date = DateTimeFormat.formatDate(details.get(4));
        int [] s_formatTime = DateTimeFormat.format24HourTime(details.get(5));
        int [] e_formatTime = DateTimeFormat.format24HourTime(details.get(6));
        int [] s_timestamp = DateTimeFormat.parseDateAndTime(formatted_date, s_formatTime[0], s_formatTime[1]);
        int [] e_timestamp = DateTimeFormat.parseDateAndTime(formatted_date, e_formatTime[0], e_formatTime[1]);

        /* Convert date/time to milliseconds */
        Calendar beginTime = Calendar.getInstance();
        beginTime.set(s_timestamp[0], s_timestamp[1] -= 1, s_timestamp[2], s_timestamp[3], s_timestamp[4]);
        long startMillis = beginTime.getTimeInMillis();

        Calendar endTime = Calendar.getInstance();
        endTime.set(e_timestamp[0], e_timestamp[1] -= 1, e_timestamp[2], e_timestamp[3], e_timestamp[4]);
        long endMillis = endTime.getTimeInMillis();

        /* Pass a URI Intent to the Calendar API along with the form data, to take you to its interface */
        Intent intent = new Intent(Intent.ACTION_INSERT)
                .setData(CalendarContract.Events.CONTENT_URI)
                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startMillis)
                .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endMillis)
                .putExtra(CalendarContract.Events.TITLE, details.get(3))
                .putExtra(CalendarContract.Events.DESCRIPTION, details.get(8))
                .putExtra(CalendarContract.Events.EVENT_LOCATION, details.get(7))
                .putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY)
                .putExtra(Intent.EXTRA_EMAIL, getEmail());
        startActivity(intent);
        onBackPressed();
    }

    private String getEmail()
    {
        if(meeting_with.equals(getSharedPreferences("MENTOR",
                Context.MODE_PRIVATE).getString("ucid", null)))
        {
            return getSharedPreferences("MENTOR",
                    Context.MODE_PRIVATE).getString("email", null);
        }
        else
            return getSharedPreferences("STUDENT",
                    Context.MODE_PRIVATE).getString("email", null);
    }

}
