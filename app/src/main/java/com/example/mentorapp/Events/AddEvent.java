package com.example.mentorapp.Events;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.provider.CalendarContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.mentorapp.Login;
import com.example.mentorapp.R;
import com.example.mentorapp.SideBar;
import com.example.mentorapp.model.DateTimeFormat;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class AddEvent extends AppCompatActivity {

    EditText event_location, event_title, event_purpose, event_start_time, event_end_time, event_date;
    TextView add_event, cancel;
    private SharedPreferences SESSION, RECEIVER, USER_TYPE;
    private DatePickerDialog dialog;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private TimePickerDialog timePickerDialog;
    private int s_hour24, s_min24, e_hour24, e_min24;
    private boolean done = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);
        USER_TYPE = getSharedPreferences("USER_TYPE", Context.MODE_PRIVATE);
        defineUserType(USER_TYPE);

        Toolbar toolbar = findViewById(R.id.toolbar);
        event_location = findViewById(R.id.event_location);
        event_title = findViewById(R.id.event_title);
        event_purpose = findViewById(R.id.event_purpose);
        event_date = findViewById(R.id.event_date);
        event_start_time = findViewById(R.id.event_start_time);
        event_end_time = findViewById(R.id.event_end_time);
        add_event = findViewById(R.id.create_event_submit);
        cancel = findViewById(R.id.cancel);

        /* Display a DatePicker Dialog for the user to choose a date when clicked upon */
        event_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                dialog = new DatePickerDialog(AddEvent.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth, mDateSetListener,
                        year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month + 1;
                event_date.setText(Integer.toString(month) + '/' + Integer.toString(dayOfMonth) + '/' +
                        Integer.toString(year));
            }
        };

        /* Display a TimePicker Dialog for the user to choose a start time when clicked upon */
        event_start_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int hour = cal.get(Calendar.HOUR_OF_DAY);
                int min = cal.get(Calendar.MINUTE);
                timePickerDialog = new TimePickerDialog(AddEvent.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                                s_hour24 = selectedHour;
                                s_min24 = selectedMinute;

                                /* Format the time to 12 hours to store it in the DB */
                                String[] data = DateTimeFormat.format12HourTime(selectedHour, selectedMinute);
                                event_start_time.setText(data[0] + ":" + data[1] + " " + data[2]);
                            }
                        }, hour, min, false);
                timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                timePickerDialog.show();
            }
        });

        /* Display a TimePicker Dialog for the user to choose a end time when clicked upon */
        event_end_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int hour = cal.get(Calendar.HOUR_OF_DAY);
                int min = cal.get(Calendar.MINUTE);
                timePickerDialog = new TimePickerDialog(AddEvent.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                                e_hour24 = selectedHour;
                                e_min24 = selectedMinute;

                                /* Format the time to 12 hours to store it in the DB */
                                String[] data = DateTimeFormat.format12HourTime(selectedHour, selectedMinute);
                                event_end_time.setText(data[0] + ":" + data[1] + " " + data[2]);
                            }
                        }, hour, min, false);
                timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                timePickerDialog.show();
            }
        });

        /* After clicking submit, collect all the form data, validate it, parse date/time to convert
         * to milliseconds, and use the Calendar API to exchange this data to its interface and save/create
         * the event. The event should be created in your Calendar application.*/

        add_event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText[] event = {event_title, event_location, event_date, event_start_time,
                        event_end_time, event_purpose};
                String email = RECEIVER.getString("email", null);
                String title = event_title.getText().toString();
                String location = event_location.getText().toString();
                String purpose = event_purpose.getText().toString();
                if (checkForm(event)) {
                    int[] d1 = DateTimeFormat.parseDateAndTime(event_date.getText().toString(), s_hour24, s_min24);
                    int[] d2 = DateTimeFormat.parseDateAndTime(event_date.getText().toString(), e_hour24, e_min24);
                    createEvent(d1, d2, title, purpose, location, email);
                } else {
                    sendAlert();
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    /* Once the create_event operation above is finished and returned you back to the app, head back
     * to your previously opened page. */
    @Override
    protected void onResume() {
        super.onResume();
        if (done) {
            onBackPressed();
        }
    }

    /* Function to create the Calendar event using its API. */
    private void createEvent(int[] start, int[] end, String title, String purpose,
                             String location, String email) {

        /* Convert date/time to milliseconds */
        Calendar beginTime = Calendar.getInstance();
        beginTime.set(start[0], start[1] -= 1, start[2], start[3], start[4]);
        long startMillis = beginTime.getTimeInMillis();

        Calendar endTime = Calendar.getInstance();
        endTime.set(end[0], end[1] -= 1, end[2], end[3], end[4]);
        long endMillis = endTime.getTimeInMillis();

        /* Pass a URI Intent to the Calendar API along with the form data, to take you to its interface */
        Intent intent = new Intent(Intent.ACTION_INSERT)
                .setData(CalendarContract.Events.CONTENT_URI)
                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startMillis)
                .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endMillis)
                .putExtra(CalendarContract.Events.TITLE, title)
                .putExtra(CalendarContract.Events.DESCRIPTION, purpose)
                .putExtra(CalendarContract.Events.EVENT_LOCATION, location)
                .putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY)
                .putExtra(Intent.EXTRA_EMAIL, email);
        this.done = true;
        startActivity(intent);
    }

    /* In case the form data wasn't properly filled out, send an alert message */
    private void sendAlert() {

        AlertDialog alert = new AlertDialog.Builder(AddEvent.this).create();
        alert.setTitle("Incomplete Form");
        alert.setMessage("Please complete all the fields.");
        alert.setButton(android.support.v7.app.AlertDialog.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alert.show();
    }

    /* Validate the form and make sure everything was filled out */
    private boolean checkForm(EditText[] event) {

        String title = event[0].getText().toString();
        String loc = event[1].getText().toString();
        String dt = event[2].getText().toString();
        String st = event[3].getText().toString();
        String et = event[4].getText().toString();
        String purpose = event[5].getText().toString();

        if (title.equals("") || loc.equals("") || dt.equals("") || st.equals("") || et.equals("")
                || purpose.equals("")) {
            return false;
        }
        return true;
    }

    /* Define who the user is to know what SharedPrefs list we use */
    private void defineUserType(SharedPreferences type)
    {
        if(type.getString("type", null).equals("student"))
        {
            SESSION = getSharedPreferences("STUDENT", MODE_PRIVATE);
            RECEIVER = getSharedPreferences("MENTOR", MODE_PRIVATE);
        }
        else if(type.getString("type", null).equals("mentor"))
        {
            SESSION = getSharedPreferences("MENTOR", MODE_PRIVATE);
            RECEIVER = getSharedPreferences("STUDENT", MODE_PRIVATE);
        }
    }
}
