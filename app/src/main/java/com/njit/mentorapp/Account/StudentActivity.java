package com.njit.mentorapp.Account;

import android.app.DatePickerDialog;
import android.content.Context;
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
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.njit.mentorapp.R;
import com.njit.mentorapp.model.Tools.DateTimeFormat;
import com.njit.mentorapp.model.Service.WebServer;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class StudentActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener
{
    SharedPreferences account, CL, USER_TYPE;
    SharedPreferences.Editor editor;
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
        account = getSharedPreferences("STUDENT", Context.MODE_PRIVATE);
        editor = account.edit();
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

        String fname = account.getString("fname", null);
        String lname = account.getString("lname", null);
        String fullname = fname + " " + lname;
        name.setText(fullname);
        full_name.setText(fullname);
        ucid.setText(account.getString("ucid", null));
        email.setText(account.getString("email", null));
        degree.setText(account.getString("degree", null));
        age.setText(account.getString("age", null));
        bday.setText(DateTimeFormat.formatDate(account.getString("birthday", null)));
        setDefaultGradeLevel(account);
        grad_date.setText(DateTimeFormat.formatDate(account.getString("grad_date", null)));
        //Picasso.get().load(account.getString("avi", null)).into(avi);
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
                    updateUserSession(editor);
                    sendAccRequest(WebServer.getQueryLink(), "updateRecord", account);
                    postToast("Account Updated");
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


    private void updateUserSession(SharedPreferences.Editor editor)
    {
        String [] name = this.name.getText().toString().split(" ");
        editor.putString("fname", name[0]);
        editor.putString("lname", name[1]);
        editor.putString("email", email.getText().toString());
        editor.putString("degree", degree.getText().toString());
        editor.putString("age", age.getText().toString());
        editor.putString("birthday", DateTimeFormat.formatDateSQL(bday.getText().toString()));
        editor.putString("grade", spinner.getSelectedItem().toString());
        editor.putString("grad_date", DateTimeFormat.formatDateSQL(grad_date.getText().toString()));
        editor.apply();
        full_name.setText(name[0] + " " + name[1]);
    }

    private void sendAccRequest(String url, final String action, final SharedPreferences ACC)
    {
        final String format_b = ACC.getString("birthday", null);
        final String format_g = ACC.getString("grad_date", null);

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
                params.put("ucid", ACC.getString("ucid", null));
                params.put("fname", ACC.getString("fname", null));
                params.put("lname", ACC.getString("lname", null));
                params.put("email", ACC.getString("email", null));
                params.put("degree", ACC.getString("degree", null));
                params.put("age", ACC.getString("age", null));
                params.put("birthday", format_b);
                params.put("grade", ACC.getString("grade", null));
                params.put("grad_date", format_g);
                return params;
            }
        };
        queue.add(request);
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

    private void setDefaultGradeLevel(SharedPreferences acc)
    {
        switch (acc.getString("grade", null))
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