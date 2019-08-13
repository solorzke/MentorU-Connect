package com.njit.mentorapp.account;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.njit.mentorapp.R;
import com.njit.mentorapp.model.service.MySingleton;
import com.njit.mentorapp.model.tools.DateTimeFormat;
import com.njit.mentorapp.model.service.WebServer;
import com.njit.mentorapp.model.tools.SetAviActivity;
import com.njit.mentorapp.model.users.Mentor;
import com.squareup.picasso.Picasso;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class MentorActivity extends AppCompatActivity
{
    private SharedPreferences USER_TYPE;
    private CircularImageView AVI;
    private EditText FNAME, LNAME, MTR_EMAIL, MTR_DATE, MTR_DEGREE, MTR_OCC, AGE, BDAY;
    private TextView EDIT, DONE, MTR_UCID, full_name, MTR_MENTEE;
    private EditText [] list;
    private Calendar calendar;
    private DatePickerDialog date;
    private Mentor mentor;
    private SwipeRefreshLayout refresh;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mentor);
        USER_TYPE = getSharedPreferences("USER_TYPE", Context.MODE_PRIVATE);
        AVI = findViewById(R.id.picasso);
        FNAME = findViewById(R.id.fname);
        LNAME = findViewById(R.id.lname);
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
        refresh = findViewById(R.id.refresh_layout);
        mentor = new Mentor(getApplicationContext());

        /* Set the toolbar */
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayOptions(android.support.v7.app.ActionBar.DISPLAY_SHOW_CUSTOM);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setTitle("Mentor");

        /* Get the mentor's info from the DB */
        getUserInfo(mentor.getUcid());
        list = new EditText [] {FNAME, LNAME, MTR_EMAIL, MTR_DATE, MTR_DEGREE, MTR_OCC, AGE, BDAY};
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
                            String day = (month + 1)+"/"+dayOfMonth+"/"+year;
                            BDAY.setText(day);
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
                            String day = (month + 1)+"/"+dayOfMonth+"/"+year;
                            MTR_DATE.setText(day);
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
                    updateChanges(mentor, WebServer.getQueryLink());
                    postToast();
                }
            });

            AVI.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(getApplicationContext(), SetAviActivity.class));
                }
            });
        }
        else{
            EDIT.setVisibility(View.INVISIBLE);
            DONE.setVisibility(View.INVISIBLE);
        }

        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getUserInfo(mentor.getUcid());
                refresh.setRefreshing(false);
            }
        });
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
            case 999999999:
                return false;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /* Check if the avi was changed recently */
    @Override
    protected void onRestart() {
        super.onRestart();
        if(!mentor.getAvi().equals(""))
            Picasso.get().load(mentor.getAvi()).into(AVI);
    }

    /* Enable or disable editing based on the user type */
    private void editText(EditText [] texts, boolean edit)
    {
        if(edit)
            for(EditText text : texts)
                text.setEnabled(true);
        else
            for(EditText text : texts)
                text.setEnabled(false);
    }

    /* Define who the user type is */
    private boolean isMentor(SharedPreferences type)
    {
        return type.getString("type", null).equals("mentor");
    }

    /* Update the shared preferences with the latest changes made */
    private void updateSharedPrefs(Mentor mentor, EditText [] texts)
    {
        mentor.setFname(texts[0].getText().toString());
        mentor.setLname(texts[1].getText().toString());
        mentor.setEmail(texts[2].getText().toString());
        mentor.setGrad_date(DateTimeFormat.formatDateSQL(texts[3].getText().toString()));
        mentor.setOccupation(texts[4].getText().toString());
        mentor.setDegree(texts[5].getText().toString());
        mentor.setAge(texts[6].getText().toString());
        mentor.setBirthday(DateTimeFormat.formatDateSQL(texts[7].getText().toString()));
        full_name.setText(mentor.getFullName());
    }

    /* Update the changes of the user info to the web server */
    private void updateChanges(final Mentor mentor, String url)
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
                params.put("action", "updateMentorRecord");
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

    /* Post a toast message */
    private void postToast()
    {
        CharSequence text = "Updated Changes.";
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(this, text, duration);
        toast.show();
    }

    /* Retrieve the user's info and their mentorship partner's info and save it into a new SharedPrefs */
    private void getUserInfo(final String user)
    {
        Map<String, String> params = new HashMap<>();
        params.put("action", "getUserInfo");
        params.put("type", "mentor");
        params.put("user", user);

        JSONObject parameters = new JSONObject(params);

        JsonObjectRequest jReq = new JsonObjectRequest(
                Request.Method.POST,
                WebServer.getQueryLink(),
                parameters,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        try{
                            Log.d("DEBUG_OUTPUT","Server Response: "+response);
                            JSONArray array = response.getJSONArray("record_m");

                            /* Store Mentee data into SharedPrefs */
                            JSONObject record = array.getJSONObject(0);
                            Mentor mentor = new Mentor(getApplicationContext(), record);
                            /* Set the AVI and User Info */
                            if(!mentor.getAvi().equals(""))
                                Picasso.get().load(mentor.getAvi()).into(AVI);
                            FNAME.setText(mentor.getFname());
                            LNAME.setText(mentor.getLname());
                            full_name.setText(mentor.getFullName());
                            MTR_EMAIL.setText(mentor.getEmail());
                            MTR_UCID.setText(mentor.getUcid());
                            MTR_DATE.setText(DateTimeFormat.formatDate(mentor.getGrad_date()));
                            MTR_DEGREE.setText(mentor.getDegree());
                            MTR_OCC.setText(mentor.getOccupation());
                            MTR_MENTEE.setText(mentor.getMentee());
                            AGE.setText(mentor.getAge());
                            BDAY.setText(DateTimeFormat.formatDate(mentor.getBirthday()));

                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("DEBUG_OUTPUT","Volley Error: "+error);
                error.printStackTrace();
                if(error instanceof TimeoutError) {
                    Toast.makeText(
                            getApplicationContext(),
                            "Request timed out. Try Again.",
                            Toast.LENGTH_SHORT
                    ).show();
                    onBackPressed();
                }
                else if(error instanceof NetworkError) {
                    Toast.makeText(
                            getApplicationContext(),
                            "Can't connect to the internet",
                            Toast.LENGTH_SHORT
                    ).show();
                    onBackPressed();
                }
            }
        });

        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(jReq);
        jReq.setRetryPolicy(new DefaultRetryPolicy(
                5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        );
    }
}