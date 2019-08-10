package com.njit.mentorapp.coaching_log;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.njit.mentorapp.model.service.NotificationText;
import com.njit.mentorapp.model.service.PushMessageToFCM;
import com.njit.mentorapp.R;
import com.njit.mentorapp.model.tools.DateTimeFormat;
import com.njit.mentorapp.model.service.WebServer;
import com.njit.mentorapp.model.tools.Validate;
import com.njit.mentorapp.model.users.User;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class RequestMeeting extends AppCompatActivity implements View.OnClickListener
{
    private EditText event_location, event_title, event_purpose, event_start_time, event_end_time, event_date;
    private TextView submit, cancel;
    private User sender, receiver;
    private String [] notifyRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_meeting);
        SharedPreferences user_type = getSharedPreferences("USER_TYPE", Context.MODE_PRIVATE);
        event_location = findViewById(R.id.event_location);
        event_title = findViewById(R.id.event_title);
        event_purpose = findViewById(R.id.event_purpose);
        event_date = findViewById(R.id.event_date);
        event_start_time = findViewById(R.id.event_start_time);
        event_end_time = findViewById(R.id.event_end_time);
        submit = findViewById(R.id.create_event_submit);
        cancel = findViewById(R.id.cancel);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null)
            getSupportActionBar().setDisplayOptions(android.support.v7.app.ActionBar.DISPLAY_SHOW_CUSTOM);

        if(Validate.isStudent(user_type))
        {
            sender = new User(getApplicationContext(), "Mentee");
            receiver = new User(getApplicationContext(), "Mentor");
            notifyRequest = NotificationText.requestMeeting(sender.getUcid());
        }
        else
        {
            sender = new User(getApplicationContext(), "Mentor");
            receiver = new User(getApplicationContext(), "Mentee");
            notifyRequest = NotificationText.requestMeeting(sender.getUcid());
        }
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        event_date.setOnClickListener(this);
        event_start_time.setOnClickListener(this);
        event_end_time.setOnClickListener(this);
        submit.setOnClickListener(this);
        cancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        Calendar calendar;
        int hour, min;
        TimePickerDialog timePickerDialog;
        DatePickerDialog date;
        switch (v.getId())
        {
            case R.id.event_date:
                calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);

                date = new DatePickerDialog(RequestMeeting.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String d = (month + 1) + "/" + dayOfMonth + "/" + year;
                        event_date.setText(d);
                    }
                }, year, month, day);
                date.show();
                break;

            case R.id.event_start_time:
                calendar = Calendar.getInstance();
                hour = calendar.get(Calendar.HOUR_OF_DAY);
                min = calendar.get(Calendar.MINUTE);
                timePickerDialog = new TimePickerDialog(RequestMeeting.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                                String [] data = DateTimeFormat.format12HourTime(selectedHour, selectedMinute);
                                String time = data[0] + ":" + data[1] + " " + data[2];
                                event_start_time.setText(time);
                            }
                        }, hour, min, false);
                if(timePickerDialog.getWindow() != null) {
                    timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.GRAY));
                    timePickerDialog.show();
                }
                break;

            case R.id.event_end_time:
                Calendar cal = Calendar.getInstance();
                hour = cal.get(Calendar.HOUR_OF_DAY);
                min = cal.get(Calendar.MINUTE);
                timePickerDialog = new TimePickerDialog(RequestMeeting.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                                String [] data = DateTimeFormat.format12HourTime(selectedHour, selectedMinute);
                                String time = data[0] + ":" + data[1] + " " + data[2];
                                event_end_time.setText(time);
                            }
                        }, hour, min, false);
                if(timePickerDialog.getWindow() != null) {
                    timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.GRAY));
                    timePickerDialog.show();
                }
                break;

            case R.id.create_event_submit:
                EditText [] event = {event_title, event_location, event_date, event_start_time,
                        event_end_time, event_purpose};
                String student = sender.getUcid();
                String mentor = receiver.getUcid();

                if(Validate.checkForm(event))
                {
                    sendMeetingRequest(WebServer.getQueryLink(), event, student, mentor);
                    CharSequence text = "Request Sent!";
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(RequestMeeting.this, text, duration);
                    toast.show();
                    PushMessageToFCM.send(
                            getApplicationContext(),
                            sender.getTopicID(getApplicationContext()),
                            notifyRequest[0],
                            notifyRequest[1]);
                    onBackPressed();
                    finish();
                }
                else{ sendAlert(); }
                break;

            case R.id.cancel:
                onBackPressed();
                finish();
                break;

            default:
                break;
        }
    }

    /* Send a Volley request with the data to the Web server to submit the request. */
    private void sendMeetingRequest(String url, final EditText [] event,
                                  final String currentUser, final String otherUser)
    {
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("DEBUG_OUTPUT","Server Response: "+response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("DEBUG_OUTPUT","Volley Error: "+error);
                error.printStackTrace();
                if(error instanceof TimeoutError)
                    Toast.makeText(
                            getApplicationContext(),
                            "Request timed out. Check your network settings.",
                            Toast.LENGTH_SHORT
                    ).show();
                else if(error instanceof NetworkError)
                    Toast.makeText(
                            getApplicationContext(),
                            "Can't connect to the internet",
                            Toast.LENGTH_SHORT
                    ).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("title", event[0].getText().toString());
                params.put("location", event[1].getText().toString());
                params.put("date", DateTimeFormat.formatDateSQL(event[2].getText().toString()));
                params.put("s_time", event[3].getText().toString());
                params.put("e_time", event[4].getText().toString());
                params.put("purpose", event[5].getText().toString());
                params.put("action", "requestMeeting");
                params.put("currentUser", currentUser);
                params.put("otherUser", otherUser);
                return params;
            }
        };
        queue.add(request);
        request.setRetryPolicy(new DefaultRetryPolicy(
                5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        );
    }

    /* Send an alert dialog to the user */
    private void sendAlert()
    {
        AlertDialog alert = new AlertDialog.Builder(RequestMeeting.this).create();
        alert.setTitle("Incomplete Form");
        alert.setMessage("Please complete all the fields.");
        alert.setButton(android.support.v7.app.AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int which){
                dialog.dismiss();
            }
        });
        alert.show();
    }
}