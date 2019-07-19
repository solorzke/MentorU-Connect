package com.njit.mentorapp.Account;

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
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.njit.mentorapp.R;
import com.njit.mentorapp.Model.Tools.DateTimeFormat;
import com.njit.mentorapp.Model.Service.WebServer;
import com.squareup.picasso.Picasso;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class MentorActivity extends AppCompatActivity  {

    SharedPreferences MENTOR, USER_TYPE;
    SharedPreferences.Editor editor;
    ImageView AVI;
    EditText MTR_NAME, MTR_EMAIL, MTR_DATE, MTR_DEGREE, MTR_OCC, AGE, BDAY;
    TextView EDIT, DONE, MTR_UCID, full_name, MTR_MENTEE;
    EditText [] list;
    Calendar calendar;
    DatePickerDialog date;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_mentor);
        MENTOR = getSharedPreferences("MENTOR", Context.MODE_PRIVATE);
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

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayOptions(android.support.v7.app.ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle("Mentor");

        Picasso.get().load(MENTOR.getString("avi", null)).into(AVI);
        MTR_NAME.setText(MENTOR.getString("fname", null) + " " + MENTOR.getString(
                "lname", null));
        full_name.setText(MENTOR.getString("fname", null) + " " + MENTOR.getString(
                "lname", null));
        MTR_EMAIL.setText(MENTOR.getString("email", null));
        MTR_UCID.setText(MENTOR.getString("ucid", null));
        MTR_DATE.setText(DateTimeFormat.formatDate(MENTOR.getString("grad_date", null)));
        MTR_DEGREE.setText(MENTOR.getString("degree", null));
        MTR_OCC.setText(MENTOR.getString("occupation", null));
        MTR_MENTEE.setText(MENTOR.getString("mentee", null));
        AGE.setText(MENTOR.getString("age", null));
        BDAY.setText(DateTimeFormat.formatDate(MENTOR.getString("birthday", null)));

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
                    updateSharedPrefs(editor, list);
                    updateChanges(MENTOR, WebServer.getQueryLink(), "updateMentorRecord");
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

    private void updateSharedPrefs(SharedPreferences.Editor e, EditText [] texts)
    {
        e = MENTOR.edit();
        String [] name = texts[0].getText().toString().split(" ");
        e.putString("fname", name[0]);
        e.putString("lname", name[1]);
        e.putString("email", texts[1].getText().toString());
        e.putString("grad_date", DateTimeFormat.formatDateSQL(texts[2].getText().toString()));
        e.putString("occupation", texts[3].getText().toString());
        e.putString("degree", texts[4].getText().toString());
        e.putString("age", texts[5].getText().toString());
        e.putString("birthday", DateTimeFormat.formatDateSQL(texts[6].getText().toString()));
        e.apply();
        full_name.setText(name[0] + " " + name[1]);
    }

    private void updateChanges(final SharedPreferences list, String url, final String action)
    {
        final String format_b = list.getString("birthday", null);
        final String format_g = list.getString("grad_date", null);

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
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("action", action);
                params.put("mentor", list.getString("ucid", null));
                params.put("fname", list.getString("fname", null));
                params.put("lname", list.getString("lname", null));
                params.put("email", list.getString("email", null));
                params.put("grad_date", format_g);
                params.put("occupation", list.getString("occupation", null));
                params.put("degree", list.getString("degree", null));
                params.put("age", list.getString("age", null));
                params.put("bday", format_b);
                return params;
            }
        };
        queue.add(request);
    }

    private void postToast()
    {
        CharSequence text = "Updated Changes.";
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(this, text, duration);
        toast.show();
    }
}