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
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AddEvent extends AppCompatActivity {

    EditText event_location, event_title, event_purpose, event_start_time, event_end_time, event_date;
    private Button submit;
    private SharedPreferences SESSION;
    private DatePickerDialog dialog;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private TimePickerDialog timePickerDialog;
    private int s_hour24, s_min24, e_hour24, e_min24;
    private boolean done = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);
        SESSION = getSharedPreferences("USER", Context.MODE_PRIVATE);
        Toolbar toolbar = findViewById(R.id.toolbar);
        event_location = findViewById(R.id.event_location);
        event_title = findViewById(R.id.event_title);
        event_purpose = findViewById(R.id.event_purpose);
        event_date = findViewById(R.id.event_date);
        event_start_time = findViewById(R.id.event_start_time);
        event_end_time = findViewById(R.id.event_end_time);
        submit = findViewById(R.id.create_event_submit);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayOptions(android.support.v7.app.ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
                                String [] data = format12HourTime(selectedHour, selectedMinute);
                                event_start_time.setText(data[0] + ":" + data[1] + " " + data[2]);
                            }
                        }, hour, min, false);
                timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                timePickerDialog.show();
            }
        });

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
                                String [] data = format12HourTime(selectedHour, selectedMinute);
                                event_end_time.setText(data[0] + ":" + data[1] + " " + data[2]);
                            }
                        }, hour, min, false);
                timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                timePickerDialog.show();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText [] event = {event_title, event_location, event_date, event_start_time,
                event_end_time, event_purpose};
                String url = "https://web.njit.edu/~kas58/mentorDemo/query.php";
                String student = SESSION.getString("ucid", null);
                String email = SESSION.getString("email", null);
                String title = event_title.getText().toString();
                String location = event_location.getText().toString();
                String purpose = event_purpose.getText().toString();
                if(checkForm(event)){
                    int [] d1 = parseDateAndTime(event_date.getText().toString(), s_hour24, s_min24);
                    int [] d2 = parseDateAndTime(event_date.getText().toString(), e_hour24, e_min24);
                    createEvent(d1, d2, title, purpose, location, email);
                    sendEventRequest(v, url, "addEvent", event, student);
                }
                else{ sendAlert(); }
            }
        });

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

    @Override
    protected void onResume() {
        super.onResume();
        if(done){
            onBackPressed();
        }
    }

    private void createEvent(int [] start, int [] end, String title, String purpose,
                             String location, String email){

        Calendar beginTime = Calendar.getInstance();
        beginTime.set(start[0], start[1]-=1, start[2], start[3], start[4]);
        long startMillis = beginTime.getTimeInMillis();

        Calendar endTime = Calendar.getInstance();
        endTime.set(end[0], end[1]-=1, end[2], end[3], end[4]);
        long endMillis = endTime.getTimeInMillis();

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

    private void sendEventRequest(View v, String url, final String action, final EditText [] event,
                                  final String student){
        RequestQueue queue = Volley.newRequestQueue(v.getContext());
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
                params.put("date", event[2].getText().toString());
                params.put("s_time", event[3].getText().toString());
                params.put("e_time", event[4].getText().toString());
                params.put("purpose", event[5].getText().toString());
                params.put("action", action);
                params.put("student", student);
                return params;
            }
        };
        queue.add(request);
    }

    private String [] format12HourTime(int hour, int min){
        int h = hour;
        String m;
        String timeset;

        if(hour > 12){  h -= 12; timeset = "PM";  }
        else if(hour == 0){  h += 12; timeset = "AM";  }
        else if(hour == 12){  timeset = "PM";  }
        else{  timeset = "AM";  }

        if(min < 10){ m = "0" + min; }
        else{  m = String.valueOf(min);  }

        String [] data = {Integer.toString(h), m, timeset};
        return data;
    }

    private int [] parseDateAndTime(String date, int hour, int min){
        String [] d = date.split("/");
        int [] dt = {Integer.parseInt(d[2]), Integer.parseInt(d[0]), Integer.parseInt(d[1]), hour, min};
        return dt;
    }

    private void sendAlert(){

        AlertDialog alert = new AlertDialog.Builder(AddEvent.this).create();
        alert.setTitle("Incomplete Form");
        alert.setMessage("Please complete all the fields.");
        alert.setButton(android.support.v7.app.AlertDialog.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int which){
                dialog.dismiss();
            }
        });
        alert.show();
    }

    private boolean checkForm(EditText [] event){

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
