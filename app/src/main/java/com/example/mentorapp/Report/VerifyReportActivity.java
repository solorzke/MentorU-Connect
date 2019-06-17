package com.example.mentorapp.Report;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.mentorapp.R;

import java.util.HashMap;
import java.util.Map;

public class VerifyReportActivity extends AppCompatActivity implements View.OnClickListener
{
    ImageView ab_img, q_1, q_2;
    EditText desc;
    TextView help_center, confirm, name;
    String reported_user, msg;
    SharedPreferences [] sharedPrefs = new SharedPreferences[3];
    String other_detail;
    boolean q1, q2;
    boolean [] list;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_report);
        ab_img = findViewById(R.id.ab_img);
        q_1 = findViewById(R.id.q_1);
        q_2 = findViewById(R.id.q_2);
        desc = findViewById(R.id.desc);
        help_center = findViewById(R.id.help_center);
        confirm = findViewById(R.id.confirm);
        name = findViewById(R.id.reported_user);
        reported_user = getIntent().getExtras().getString("name");
        msg = getIntent().getExtras().getString("msg");
        list = getIntent().getExtras().getBooleanArray("reasons");
        other_detail = getIntent().getExtras().getString("other_detail");
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayOptions(android.support.v7.app.ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        q1 = false;
        q2 = false;
        sharedPrefs[0] = getSharedPreferences("USER_TYPE", Context.MODE_PRIVATE);
        sharedPrefs[1] = getSharedPreferences("STUDENT", Context.MODE_PRIVATE);
        sharedPrefs[2] = getSharedPreferences("MENTOR", Context.MODE_PRIVATE);
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        name.setText(reported_user);
        confirm.setOnClickListener(this);
        q_1.setOnClickListener(this);
        q_2.setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.confirm:
                String sentinel="";
                if(q1){ sentinel = "yes"; }
                else if (q2){ sentinel = "no"; }
                submitReport(msg, sharedPrefs, sentinel);
                startActivity(new Intent(getApplicationContext(), ConfirmActivity.class));
                break;

            case R.id.q_1:
                /* If radio button is pressed already */
                if(q1)
                {
                    this.q1 = false;
                    this.q_1.setImageResource(android.R.drawable.radiobutton_off_background);
                }
                /* If radio button isn't pressed already */
                else
                {
                    this.q1 = true;
                    this.q2 = false;
                    this.q_2.setImageResource(android.R.drawable.radiobutton_off_background);
                    this.q_1.setImageResource(android.R.drawable.radiobutton_on_background);
                }
                break;

            case R.id.q_2:
                /* If radio button is pressed already */
                if(q2)
                {
                    this.q2 = false;
                    this.q_2.setImageResource(android.R.drawable.radiobutton_off_background);
                }
                /* If radio button isn't pressed already */
                else
                {
                    this.q2 = true;
                    this.q1 = false;
                    this.q_1.setImageResource(android.R.drawable.radiobutton_off_background);
                    this.q_2.setImageResource(android.R.drawable.radiobutton_on_background);
                }
                break;

            default:
                break;
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

    private void submitReport(final String msg, SharedPreferences [] users, final String sentinel)
    {
        String [] array = isStudent(users);
        final String reportingUser = array[0];
        final String reportedUser = array[1];

        String url = "https://web.njit.edu/~kas58/mentorDemo/Model/index.php";

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("Server Response: "+response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("Volley Error: "+error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("reportedUser", reportedUser);
                params.put("reportingUser", reportingUser);
                params.put("msg", msg);
                params.put("desc", desc.getText().toString());
                params.put("r1", trueOrFalse(list[0]));
                params.put("r2", trueOrFalse(list[1]));
                params.put("r3", trueOrFalse(list[2]));
                params.put("other", trueOrFalse(list[3]));
                params.put("other_detail", other_detail);
                params.put("moreThanOnce", sentinel);
                params.put("action", "report");
                return params;
            }
        };
        queue.add(request);
    }

    private String trueOrFalse(boolean check)
    {
        String result;
        if(check)
        {
            result = "true";
        }
        else
        {
            result = "false";
        }
        return result;
    }

    private String [] isStudent(SharedPreferences [] users)
    {
        /* List [] => ['reporting user'], ['reported user']; */
        String [] list = new String[2];

        if (users[0].getString("type", null).equals("student"))
        {
            list[0] = users[1].getString("ucid", null);
            list[1] = users[2].getString("ucid", null);
        }
        else {
            list[0] = users[2].getString("ucid", null);
            list[1] = users[1].getString("ucid", null);
        }
        return list;
    }
}