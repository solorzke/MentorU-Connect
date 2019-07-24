package com.njit.mentorapp.fab;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.njit.mentorapp.R;
import com.njit.mentorapp.model.service.WebServer;
import com.njit.mentorapp.model.users.Mentee;
import com.njit.mentorapp.model.users.Mentor;
import java.util.HashMap;
import java.util.Map;

public class EditGoals extends AppCompatActivity
{
    EditText g1, g2, g3, g4;
    TextView cancel, save;
    RelativeLayout r1, r2, r3, r4;
    String[] goals = new String [4];
    EditText[] group;
    private Mentor mentor;
    private Mentee mentee;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_goals);
        g1 = findViewById(R.id.g1);
        g2 = findViewById(R.id.g2);
        g3 = findViewById(R.id.g3);
        g4 = findViewById(R.id.g4);
        r1 = findViewById(R.id.r1);
        r2 = findViewById(R.id.r2);
        r3 = findViewById(R.id.r3);
        r4 = findViewById(R.id.r4);
        cancel = findViewById(R.id.cancel);
        save = findViewById(R.id.save);
        mentor = new Mentor(getApplicationContext());
        mentee = new Mentee(getApplicationContext());
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayOptions(android.support.v7.app.ActionBar.DISPLAY_SHOW_CUSTOM);
        toolbar.setTitle("Edit Goals");
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        g1.setText(getIntent().getExtras().getString("goal1"));
        g2.setText(getIntent().getExtras().getString("goal2"));
        g3.setText(getIntent().getExtras().getString("goal3"));
        g4.setText(getIntent().getExtras().getString("goal4"));
        group = new EditText[]{g1, g2, g3, g4};

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

                updateGoals("updateGoals", mentor.getUcid(), mentee.getUcid(), goals);
                postToast();
                onBackPressed();
                finish();
            }
        });

        r1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertMsg(g1, g2, g3, g4);
            }
        });

        r2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertMsg(g2, g1, g3, g4);
            }
        });

        r3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertMsg(g3, g2, g1, g4);
            }
        });

        r4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertMsg(g4, g2, g3, g1);
            }
        });


    }

    private void updateGoals(final String action, final String mentor, final String student, final String[] goals)
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
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("action", action);
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
    }

    private void alertMsg(final EditText cur_goal, final EditText goal1, final EditText goal2, final EditText goal3)
    {
        AlertDialog dialog = new AlertDialog.Builder(this).create();
        dialog.setTitle("Goal Action");
        dialog.setMessage("Select option to edit/remove");
        dialog.setButton(android.app.AlertDialog.BUTTON_POSITIVE, "Edit", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                cur_goal.setEnabled(true);
                goal1.setEnabled(false);
                goal2.setEnabled(false);
                goal3.setEnabled(false);
            }
        });
        dialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Remove", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                cur_goal.setText("");
            }
        });
        dialog.show();
    }

    private void postToast()
    {
        Context context = getApplicationContext();
        CharSequence text = "Goal Changes Saved";
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

}