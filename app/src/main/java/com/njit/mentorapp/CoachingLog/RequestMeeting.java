package com.njit.mentorapp.CoachingLog;

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
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.njit.mentorapp.R;
import com.njit.mentorapp.model.DateTimeFormat;
import com.njit.mentorapp.model.Service.WebServer;
import com.njit.mentorapp.model.Validate;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class RequestMeeting extends AppCompatActivity implements View.OnClickListener
{
    EditText event_location, event_title, event_purpose, event_start_time, event_end_time, event_date;
    private TextView submit, cancel;
    private SharedPreferences sender, receiver, user_type;
    private DatePickerDialog date;
    private TimePickerDialog timePickerDialog;
    private Calendar calendar;
    int hour, min;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_meeting);
        user_type = getSharedPreferences("USER_TYPE", Context.MODE_PRIVATE);
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
        getSupportActionBar().setDisplayOptions(android.support.v7.app.ActionBar.DISPLAY_SHOW_CUSTOM);

        if(Validate.isStudent(user_type))
        {
            sender = getSharedPreferences("STUDENT", Context.MODE_PRIVATE);
            receiver = getSharedPreferences("MENTOR", Context.MODE_PRIVATE);
        }
        else
        {
            sender = getSharedPreferences("MENTOR", Context.MODE_PRIVATE);
            receiver = getSharedPreferences("STUDENT", Context.MODE_PRIVATE);
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
                        event_date.setText((month + 1)+"/"+dayOfMonth+"/"+year);
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
                                event_start_time.setText(data[0] + ":" + data[1] + " " + data[2]);
                            }
                        }, hour, min, false);
                timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.GRAY));
                timePickerDialog.show();
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
                                event_end_time.setText(data[0] + ":" + data[1] + " " + data[2]);
                            }
                        }, hour, min, false);
                timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.GRAY));
                timePickerDialog.show();
                break;

            case R.id.create_event_submit:
                EditText [] event = {event_title, event_location, event_date, event_start_time,
                        event_end_time, event_purpose};
                String student = sender.getString("ucid", null);
                String mentor = receiver.getString("ucid", null);

                if(Validate.checkForm(event))
                {
                    sendMeetingRequest(WebServer.getQueryLink(), "requestMeeting", event, student, mentor);
                    CharSequence text = "Request Sent!";
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(RequestMeeting.this, text, duration);
                    toast.show();
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

    private void sendMeetingRequest(String url, final String action, final EditText [] event,
                                  final String currentUser, final String otherUser)
    {
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("title", event[0].getText().toString());
                params.put("location", event[1].getText().toString());
                params.put("date", DateTimeFormat.formatDateSQL(event[2].getText().toString()));
                params.put("s_time", event[3].getText().toString());
                params.put("e_time", event[4].getText().toString());
                params.put("purpose", event[5].getText().toString());
                params.put("action", action);
                params.put("currentUser", currentUser);
                params.put("otherUser", otherUser);
                return params;
            }
        };
        queue.add(request);
    }

    private void sendAlert()
    {
        AlertDialog alert = new AlertDialog.Builder(RequestMeeting.this).create();
        alert.setTitle("Incomplete Form");
        alert.setMessage("Please complete all the fields.");
        alert.setButton(android.support.v7.app.AlertDialog.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int which){
                dialog.dismiss();
            }
        });
        alert.show();
    }
}