/* Registration is only used for Mentee type users. Mentors should be registered through the program
 * itself since they go through a vastly different interview process before they're accepted compared
 * to mentees.
 *
 * The mentee MUST be assigned to a mentor already before registering or else registration
 * will be denied. Same with Login process. The Mentor U Program determines which mentee the mentor
 * will be assigned to before pairing them together on the DB by the Administrator.
 * The class below searches for their mentor info before registering below.*/

package com.njit.mentorapp;

import android.app.DatePickerDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RequestQueue;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.njit.mentorapp.model.service.FireBaseServer;
import com.njit.mentorapp.model.tools.DateTimeFormat;
import com.njit.mentorapp.model.tools.JSON;
import com.njit.mentorapp.model.service.WebServer;
import com.njit.mentorapp.model.tools.VolleyCallback;
import android.content.Intent;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class Registration extends AppCompatActivity implements AdapterView.OnItemSelectedListener
{
    String email,ucid,password,degree,age,bday,grade,grad_date, f_nam, l_nam;
    ImageView back;
    EditText  em, id, pass, dg, ag, by, gd_dte, fname, lname;
    TextView gde;
    Button reg;
    Calendar calendar;
    DatePickerDialog date;
    Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        reg = findViewById(R.id.button);
        back = findViewById(R.id.back);
        fname = findViewById(R.id.fname);
        lname = findViewById(R.id.lname);
        em = findViewById(R.id.Email);
        id = findViewById(R.id.UCID);
        pass = findViewById(R.id.Pass);
        dg = findViewById(R.id.degree);
        ag = findViewById(R.id.age);
        by = findViewById(R.id.bday);
        gde = findViewById(R.id.grade);
        gd_dte = findViewById(R.id.grad_date);
        spinner = findViewById(R.id.spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.grade_level, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
    }


    @Override
    protected void onStart()
    {
        super.onStart();

        by.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);

                date = new DatePickerDialog(Registration.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        by.setText((month + 1)+"/"+dayOfMonth+"/"+year);
                    }
                }, year, month, day);
                date.show();
            }
        });

        gd_dte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);

                date = new DatePickerDialog(Registration.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        gd_dte.setText((month + 1)+"/"+dayOfMonth+"/"+year);
                    }
                }, year, month, day);
                date.show();
            }
        });

        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //--------------------
                f_nam = fname.getText().toString();
                l_nam = lname.getText().toString();
                email = em.getText().toString();
                ucid = id.getText().toString();
                password = pass.getText().toString();
                degree = dg.getText().toString();
                age = ag.getText().toString();
                bday = by.getText().toString();
                grade = spinner.getSelectedItem().toString();
                grad_date = gd_dte.getText().toString();
                register();
            }

        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    /* Register with the data entered, and analyze the response from the server. If successful,
     * it should've created an account and created 6 empty courses to fill in later, plus create
     * a topic id (id for their notification channel used with) FireBase only for setting its
     * notification channel with mentor. */
    private void register()
    {
        final String format_b = DateTimeFormat.formatDateSQL(bday);
        final String format_g = DateTimeFormat.formatDateSQL(grad_date);
        RequestQueue rq = Volley.newRequestQueue(this);
        StringRequest sq = new StringRequest(Request.Method.POST, WebServer.getRegisterLink(),
                new Response.Listener<String>() {
            @Override
            public void onResponse(String response)
            {
                Log.d("DEBUG_OUTPUT","Server Response: "+response);
                if (response.contains("REGISTERED"))
                {
                    String mentor = response.split("\\|")[1];
                    FireBaseServer.setTopicID(getApplicationContext(), new String[]{ucid, mentor}, new VolleyCallback() {
                        @Override
                        public void onCallback(String callback) {
                            if(callback.equals("Topic ID created!"))
                                Log.d("DEBUG_OUTPUT", callback);
                            else
                                Log.d("DEBUG_OUTPUT", callback);
                        }
                    });
                    Intent gth = new Intent(getApplicationContext(), JSON.class);
                    gth.putExtra("com.example.mentorapp.CONFIRM", "student");
                    gth.putExtra("com.example.mentorapp.UCID", ucid);
                    startActivity(gth);
                }
                else if (response.contains("STUDENT_EXISTS"))
                    Toast.makeText(
                            getApplicationContext(),
                            "Account Already Exists. Return To Login Page",
                            Toast.LENGTH_SHORT
                    ).show();

                else if (response.contains("NO_MENTOR_ASSIGNED"))
                    Toast.makeText(
                            getApplicationContext(),
                            "You aren't assigned to a mentor yet. Contact MentorU for more information.",
                            Toast.LENGTH_SHORT
                    ).show();

                else if (response.contains("AUTHENTICATION_FAIL"))
                    Toast.makeText(
                            getApplicationContext(),
                            "UCID Not Found, Please see HelpDesk",
                            Toast.LENGTH_SHORT
                    ).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Log.d("DEBUG_OUTPUT","Volley Error: "+error);
                if(error instanceof TimeoutError)
                    Toast.makeText(
                            getApplicationContext(),
                            "Request timed out. Try Again.",
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
                params.put("tableName", "Students");
                params.put("username", ucid);
                params.put("password", password);
                params.put("fname", f_nam);
                params.put("lname", l_nam);
                params.put("email", email);
                params.put("degree", degree);
                params.put("age", age);
                params.put("grade", grade);
                params.put("grad_date", format_g);
                params.put("bday", format_b);
                return params;
            }
        };
        rq.add(sq);
        sq.setRetryPolicy(new DefaultRetryPolicy(
                3000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        );
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
    {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent)
    {

    }
}