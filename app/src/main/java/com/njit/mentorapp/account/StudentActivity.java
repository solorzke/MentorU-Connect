package com.njit.mentorapp.account;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
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
import com.njit.mentorapp.model.tools.SetAviActivity;
import com.njit.mentorapp.model.users.Mentee;
import com.squareup.picasso.Picasso;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class StudentActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener
{
    private Mentee mentee;
    SharedPreferences USER_TYPE;
    TextView edit, done, ucid, full_name;
    EditText name, email, degree, age, bday, grad_date;
    ImageView avi;
    EditText [] editable = new EditText[6];
    Calendar calendar;
    DatePickerDialog date;
    Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_student_beta);
        USER_TYPE = getSharedPreferences("USER_TYPE", Context.MODE_PRIVATE);
        mentee = new Mentee(getApplicationContext());
        edit = findViewById(R.id.edit_acc_btn);
        done = findViewById(R.id.done_acc_btn);
        name = findViewById(R.id.acc_stu_name_1);
        ucid = findViewById(R.id.acc_stu_ucid_1);
        email = findViewById(R.id.acc_stu_email_1);
        degree = findViewById(R.id.acc_stu_degree_1);
        age = findViewById(R.id.age);
        bday = findViewById(R.id.bday);
        grad_date = findViewById(R.id.grad_date);
        avi = findViewById(R.id.avi);
        Toolbar toolbar = findViewById(R.id.toolbar);
        spinner = findViewById(R.id.spinner);
        full_name = findViewById(R.id.fullname);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.grade_level, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayOptions(android.support.v7.app.ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle("Mentee");

        String fullname = mentee.getFname() + " " + mentee.getLname();
        name.setText(fullname);
        full_name.setText(fullname);
        ucid.setText(mentee.getUcid());
        email.setText(mentee.getEmail());
        degree.setText(mentee.getDegree());
        age.setText(mentee.getAge());
        bday.setText(DateTimeFormat.formatDate(mentee.getBirthday()));
        setDefaultGradeLevel(mentee);
        grad_date.setText(DateTimeFormat.formatDate(mentee.getGrad_date()));
        if(!mentee.getAvi().equals(""))
            Picasso.get().load(mentee.getAvi()).into(avi);
    }

    @Override
    public void onStart()
    {
        super.onStart();
        editable = new EditText[]{name, email, degree, age, bday, grad_date};
        if(isStudent(USER_TYPE))
        {
            bday.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    calendar = Calendar.getInstance();
                    int day = calendar.get(Calendar.DAY_OF_MONTH);
                    int month = calendar.get(Calendar.MONTH);
                    int year = calendar.get(Calendar.YEAR);

                    date = new DatePickerDialog(StudentActivity.this, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            bday.setText((month + 1)+"/"+dayOfMonth+"/"+year);
                        }
                    }, year, month, day);
                    date.show();
                }
            });

            grad_date.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    calendar = Calendar.getInstance();
                    int day = calendar.get(Calendar.DAY_OF_MONTH);
                    int month = calendar.get(Calendar.MONTH);
                    int year = calendar.get(Calendar.YEAR);

                    date = new DatePickerDialog(StudentActivity.this, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            grad_date.setText((month + 1)+"/"+dayOfMonth+"/"+year);
                        }
                    }, year, month, day);
                    date.show();
                }
            });

            disableEditAccText(editable);
            edit.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v) {
                    edit.setVisibility(View.INVISIBLE);
                    done.setVisibility(View.VISIBLE);
                    enableEditAccText(editable);
                }
            });

            done.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    done.setVisibility(View.INVISIBLE);
                    edit.setVisibility(View.VISIBLE);
                    disableEditAccText(editable);
                    updateUserSession(mentee);
                    sendAccRequest(WebServer.getQueryLink(), "updateRecord");
                    postToast("Account Updated");
                }
            });

            avi.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(getApplicationContext(), SetAviActivity.class));
                }
            });
        }
        else {
            disableEditAccText(editable);
            edit.setVisibility(View.INVISIBLE);
            done.setVisibility(View.INVISIBLE);
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

    @Override
    protected void onRestart() {
        super.onRestart();
        if(!mentee.getAvi().equals(""))
            Picasso.get().load(mentee.getAvi()).into(avi);
    }

    private void disableEditAccText(EditText [] editables)
    {
        /* Editable [] => ['name', 'email', 'degree', 'age', 'birthday', 'grad_date']; */

        for(EditText editable : editables)
        {
            editable.setFocusableInTouchMode(false);
            editable.setEnabled(false);
            editable.setCursorVisible(false);
            editable.setBackgroundColor(Color.TRANSPARENT);
        }
        spinner.setClickable(false);
    }

    private void enableEditAccText(EditText [] editables)
    {
        /* Editable [] => ['name', 'email', 'degree', 'age', 'birthday', 'grad_date']; */

        for(EditText editable : editables)
        {
            editable.setFocusableInTouchMode(true);
            editable.setEnabled(true);
            editable.setCursorVisible(true);
            editable.setBackgroundColor(Color.TRANSPARENT);
        }
        editables[4].setFocusableInTouchMode(false);
        editables[5].setFocusableInTouchMode(false);
        spinner.setClickable(true);
    }


    private void updateUserSession(Mentee mentee)
    {
        String [] name = this.name.getText().toString().split(" ");
        mentee.setFname(name[0]);
        mentee.setLname(name[1]);
        mentee.setEmail(email.getText().toString());
        mentee.setDegree(degree.getText().toString());
        mentee.setAge(age.getText().toString());
        mentee.setBirthday(DateTimeFormat.formatDateSQL(bday.getText().toString()));
        mentee.setGrade(spinner.getSelectedItem().toString());
        mentee.setGrad_date(DateTimeFormat.formatDateSQL(grad_date.getText().toString()));
        full_name.setText(name[0] + " " + name[1]);
    }

    private void sendAccRequest(String url, final String action)
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
                params.put("ucid", mentee.getUcid());
                params.put("fname", mentee.getFname());
                params.put("lname", mentee.getLname());
                params.put("email", mentee.getEmail());
                params.put("degree", mentee.getDegree());
                params.put("age", mentee.getAge());
                params.put("birthday", mentee.getBirthday());
                params.put("grade", mentee.getGrade());
                params.put("grad_date", mentee.getGrad_date());
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

    private void postToast(String msg)
    {
        CharSequence text = msg;
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(this, text, duration);
        toast.show();
    }

    private boolean isStudent(SharedPreferences type)
    {
        if(type.getString("type", null).equals("student"))
        {
            return true;
        }
        else{
            return false;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {  }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {  }

    private void setDefaultGradeLevel(Mentee acc)
    {
        switch (acc.getGrade())
        {
            case "Freshman":
                this.spinner.setSelection(0);
                break;

            case "Sophomore":
                this.spinner.setSelection(1);
                break;

            case "Junior":
                this.spinner.setSelection(2);
                break;

            case "Senior":
                this.spinner.setSelection(3);
                break;

            default:
                break;
        }
    }
}