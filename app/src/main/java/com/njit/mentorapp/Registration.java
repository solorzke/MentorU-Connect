package com.njit.mentorapp;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.njit.mentorapp.model.service.FireBaseServer;
import com.njit.mentorapp.model.tools.DateTimeFormat;
import com.njit.mentorapp.model.tools.JSON;
import com.njit.mentorapp.model.service.WebServer;
import android.content.Intent;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class Registration extends AppCompatActivity implements AdapterView.OnItemSelectedListener
{
    String name,email,ucid,password,degree,age,bday,grade,grad_date;
    ImageView back;
    String[] arr;
    EditText nam, em, id, pass, dg, ag, by, gd_dte;
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
        reg = (Button) findViewById(R.id.button);
        back = findViewById(R.id.back);
        nam = (EditText) findViewById(R.id.Name);
        em = (EditText) findViewById(R.id.Email);
        id = (EditText) findViewById(R.id.UCID);
        pass = (EditText) findViewById(R.id.Pass);
        dg = (EditText) findViewById(R.id.degree);
        ag = (EditText) findViewById(R.id.age);
        by = (EditText) findViewById(R.id.bday);
        gde = findViewById(R.id.grade);
        gd_dte = (EditText) findViewById(R.id.grad_date);
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
                name = nam.getText().toString();
                arr = name.split(" ");
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

    private void register()
    {
        final String format_b = DateTimeFormat.formatDateSQL(bday);
        final String format_g = DateTimeFormat.formatDateSQL(grad_date);
        RequestQueue rq = Volley.newRequestQueue(this);
        StringRequest sq = new StringRequest(Request.Method.POST, WebServer.getRegisterLink(),
                new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("DEBUG_OUTPUT","Server Response: "+response);
                if (response.contains("REGISTERED")) {
                    String mentor = response.split("\\|")[1];
                    FireBaseServer.registerToDB(ucid, mentor);
                    Intent gth = new Intent(getApplicationContext(), JSON.class);
                    gth.putExtra("com.example.mentorapp.CONFIRM", "student");
                    gth.putExtra("com.example.mentorapp.UCID", ucid);
                    startActivity(gth);

                } else if (response.contains("STUDENT_EXISTS")) {
                    String message = "Account Already Exists. Return To Login Page";
                    alertMessage("Alert", message, "OK");

                } else if (response.contains("NO_MENTOR_ASSIGNED")) {
                    String message = "You aren't assigned to a mentor yet. Contact MentorU for more information.";
                    alertMessage("Alert", message, "OK");

                } else if (response.contains("AUTHENTICATION_FAIL")) {
                    String message = "UCID Not Found, Please see HelpDesk";
                    alertMessage("Alert", message, "OK");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("DEBUG_OUTPUT","Volley Error: "+error);
                String message = "Couldn't connect with the NJIT server. Try Again.";
                alertMessage("Error 500: Internal Server Error", message, "OK");

            }

        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("tableName", "Students");
                params.put("username", ucid);
                params.put("password", password);
                params.put("fname", arr[0]);
                params.put("lname", arr[1]);
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
    }

    private void alertMessage(String title, String message, String button)
    {
        AlertDialog alert = new AlertDialog.Builder(Registration.this).create();
        alert.setTitle(title);
        alert.setMessage(message);
        alert.setButton(AlertDialog.BUTTON_NEUTRAL, button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                onBackPressed();
            }
        });
        alert.show();
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