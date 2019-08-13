package com.njit.mentorapp.fab;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.njit.mentorapp.R;
import com.njit.mentorapp.sidebar.SideBar;
import com.njit.mentorapp.model.service.WebServer;
import com.njit.mentorapp.model.users.Mentee;
import com.njit.mentorapp.model.users.Mentor;
import java.util.HashMap;
import java.util.Map;

public class EditGoals extends AppCompatActivity
{
    private EditText g1, g2, g3, g4;
    private TextView cancel, save;
    private String[] goals = new String [4];
    private EditText[] group;
    private Mentor mentor;
    private Mentee mentee;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        setTheme(R.style.AppTheme_NoActionBar);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_goals);
        g1 = findViewById(R.id.g1);
        g2 = findViewById(R.id.g2);
        g3 = findViewById(R.id.g3);
        g4 = findViewById(R.id.g4);
        cancel = findViewById(R.id.cancel);
        save = findViewById(R.id.save);
        mentor = new Mentor(getApplicationContext());
        mentee = new Mentee(getApplicationContext());
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        if(getIntent().getExtras() != null) {
            g1.setHint(getIntent().getExtras().getString("goal1"));
            g2.setHint(getIntent().getExtras().getString("goal2"));
            g3.setHint(getIntent().getExtras().getString("goal3"));
            g4.setHint(getIntent().getExtras().getString("goal4"));
            group = new EditText[]{g1, g2, g3, g4};
        }

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < 4; i++)
                    goals[i] = group[i].getText().toString();

                if(!isBlank(goals))
                {
                    updateGoals(mentor.getUcid(), mentee.getUcid(), goals);
                    postToast();
                    startActivity(new Intent(getApplicationContext(), SideBar.class));
                    finish();
                }
                else
                    Toast.makeText(
                            getApplicationContext(),
                            "Provide 4 goals please",
                            Toast.LENGTH_SHORT
                    ).show();
            }
        });
    }

    /* Update the goal text changes to the database via server */
    private void updateGoals(final String mentor, final String student, final String[] goals)
    {
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        StringRequest request = new StringRequest(Request.Method.POST, WebServer.getQueryLink(), new Response.Listener<String>() {
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
                params.put("action", "updateGoals");
                params.put("mentor", mentor);
                params.put("student", student);
                params.put("g1", goals[0]);
                params.put("g2", goals[1]);
                params.put("g3", goals[2]);
                params.put("g4", goals[3]);
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
        Context context = getApplicationContext();
        CharSequence text = "Goal Changes Saved";
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    private boolean isBlank(String [] goals)
    {
        for(String goal : goals)
            if(goal.equals("") || goal.equals(" ") || goal.contains("No new goals from"))
                return true;
        return false;
    }
}