package com.njit.mentorapp.account;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
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
import com.njit.mentorapp.R;
import com.njit.mentorapp.model.tools.DateTimeFormat;
import com.njit.mentorapp.model.service.WebServer;
import com.njit.mentorapp.model.users.Mentor;
import com.squareup.picasso.Picasso;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class MentorActivity extends AppCompatActivity  {

    SharedPreferences USER_TYPE;
    SharedPreferences.Editor editor;
    ImageView AVI;
    EditText MTR_NAME, MTR_EMAIL, MTR_DATE, MTR_DEGREE, MTR_OCC, AGE, BDAY;
    TextView EDIT, DONE, MTR_UCID, full_name, MTR_MENTEE;
    EditText [] list;
    Calendar calendar;
    DatePickerDialog date;
    private Mentor mentor;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_mentor);
        USER_TYPE = getSharedPreferences("USER_TYPE", Context.MODE_PRIVATE);
        AVI = findViewById(R.id.picasso);
        MTR_NAME = findViewById(R.id.acc_mtr_name);
        MTR_EMAIL = findViewById(R.id.acc_mtr_email);
        MTR_UCID = findViewById(R.id.mtr_ucid1);
        MTR_DATE = findViewById(R.id.mtr_date1);
        MTR_DEGREE = findViewById(R.id.mtr_degree1);
        MTR_OCC = findViewById(R.id.mtr_occ1);
        MTR_MENTEE = findViewById(R.id.mtr_mentee1);
        EDIT = findViewById(R.id.m_edit);
        DONE = findViewById(R.id.m_done);
        AGE = findViewById(R.id.age);
        BDAY = findViewById(R.id.bday);
        full_name = findViewById(R.id.fullname);
        mentor = new Mentor(getApplicationContext());

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayOptions(android.support.v7.app.ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle("Mentor");

        Picasso.get().load(mentor.getAvi()).into(AVI);
        MTR_NAME.setText(mentor.getFname() + " " + mentor.getLname());
        full_name.setText(mentor.getFname() + " " + mentor.getLname());
        MTR_EMAIL.setText(mentor.getEmail());
        MTR_UCID.setText(mentor.getUcid());
        MTR_DATE.setText(DateTimeFormat.formatDate(mentor.getGrad_date()));
        MTR_DEGREE.setText(mentor.getDegree());
        MTR_OCC.setText(mentor.getOccupation());
        MTR_MENTEE.setText(mentor.getMentee());
        AGE.setText(mentor.getAge());
        BDAY.setText(DateTimeFormat.formatDate(mentor.getBirthday()));

        list = new EditText [] {MTR_NAME, MTR_EMAIL, MTR_DATE, MTR_DEGREE, MTR_OCC, AGE, BDAY};
    }

    @Override
    public void onStart()
    {
        super.onStart();
        if(isMentor(USER_TYPE))
        {
            EDIT.setVisibility(View.VISIBLE);
            DONE.setVisibility(View.INVISIBLE);

            BDAY.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    calendar = Calendar.getInstance();
                    int day = calendar.get(Calendar.DAY_OF_MONTH);
                    int month = calendar.get(Calendar.MONTH);
                    int year = calendar.get(Calendar.YEAR);

                    date = new DatePickerDialog(MentorActivity.this, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            BDAY.setText((month + 1)+"/"+dayOfMonth+"/"+year);
                        }
                    }, year, month, day);
                    date.show();
                }
            });

            MTR_DATE.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    calendar = Calendar.getInstance();
                    int day = calendar.get(Calendar.DAY_OF_MONTH);
                    int month = calendar.get(Calendar.MONTH);
                    int year = calendar.get(Calendar.YEAR);

                    date = new DatePickerDialog(MentorActivity.this, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            MTR_DATE.setText((month + 1)+"/"+dayOfMonth+"/"+year);
                        }
                    }, year, month, day);
                    date.show();
                }
            });

            EDIT.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DONE.setVisibility(View.VISIBLE);
                    EDIT.setVisibility(View.INVISIBLE);
                    editText(list, true);
                }
            });

            DONE.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DONE.setVisibility(View.INVISIBLE);
                    EDIT.setVisibility(View.VISIBLE);
                    editText(list, false);
                    updateSharedPrefs(mentor, list);
                    updateChanges(mentor, WebServer.getQueryLink(), "updateMentorRecord");
                    postToast();
                }
            });
        }
        else{
            EDIT.setVisibility(View.INVISIBLE);
            DONE.setVisibility(View.INVISIBLE);
        }
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

    private void editText(EditText [] texts, boolean edit)
    {

        if(edit)
        {
            for(int i = 0; i < texts.length; i++){
                texts[i].setEnabled(true);
            }
        }
        else{
            for(int i = 0; i < texts.length; i++){
                texts[i].setEnabled(false);
            }
        }
    }

    private boolean isMentor(SharedPreferences type)
    {
        if(type.getString("type", null).equals("mentor"))
        {
            return true;
        }
        else{
            return false;
        }
    }

    private void updateSharedPrefs(Mentor mentor, EditText [] texts)
    {
        String [] name = texts[0].getText().toString().split(" ");
        mentor.setFname(name[0]);
        mentor.setLname(name[1]);
        mentor.setEmail(texts[1].getText().toString());
        mentor.setGrad_date(DateTimeFormat.formatDateSQL(texts[2].getText().toString()));
        mentor.setOccupation(texts[3].getText().toString());
        mentor.setDegree(texts[4].getText().toString());
        mentor.setAge(texts[5].getText().toString());
        mentor.setBirthday(DateTimeFormat.formatDateSQL(texts[6].getText().toString()));
        full_name.setText(name[0] + " " + name[1]);
    }

    private void updateChanges(final Mentor mentor, String url, final String action)
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
                Map<String, String> params = new HashMap<String, String>();
                params.put("action", action);
                params.put("mentor", mentor.getUcid());
                params.put("fname", mentor.getFname());
                params.put("lname", mentor.getLname());
                params.put("email", mentor.getEmail());
                params.put("grad_date", mentor.getGrad_date());
                params.put("occupation", mentor.getOccupation());
                params.put("degree", mentor.getDegree());
                params.put("age", mentor.getAge());
                params.put("bday", mentor.getBirthday());
                return params;
            }
        };
        queue.add(request);
        request.setRetryPolicy(new DefaultRetryPolicy(
                3000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        );
    }

    private void postToast()
    {
        CharSequence text = "Updated Changes.";
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(this, text, duration);
        toast.show();
    }
}