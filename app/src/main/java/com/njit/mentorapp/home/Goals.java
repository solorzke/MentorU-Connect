package com.njit.mentorapp.home;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.njit.mentorapp.account.MentorActivity;
import com.njit.mentorapp.fab.EditGoals;
import com.njit.mentorapp.model.service.NotificationText;
import com.njit.mentorapp.model.service.PushMessageToFCM;
import com.njit.mentorapp.R;
import com.njit.mentorapp.toolbar.SendEmail;
import com.njit.mentorapp.model.tools.DateTimeFormat;
import com.njit.mentorapp.model.service.WebServer;
import com.njit.mentorapp.model.users.Mentee;
import com.njit.mentorapp.model.users.Mentor;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class Goals extends Fragment implements View.OnClickListener
{
    SharedPreferences USER_TYPE;
    ImageView CHECKMARK_1, CHECKMARK_2, CHECKMARK_3, CHECKMARK_4;
    FloatingActionButton fab;
    TextView no_goals, GOAL_1, GOAL_2, GOAL_3, GOAL_4, SEMESTER, WEEKS, ACCOUNT, EMAIL, PERCENT;
    String url = WebServer.getQueryLink();
    String [] notifyText;
    Boolean c1 = false, c2 = false, c3 = false, c4 = false;
    View view;
    LinearLayout goals_layout;
    private Mentee mentee;
    private Mentor mentor;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.fragment_goals, container, false);
        mentee = new Mentee(view.getContext());
        mentor = new Mentor(view.getContext());
        USER_TYPE = getActivity().getSharedPreferences("USER_TYPE", Context.MODE_PRIVATE);
        fab = view.findViewById(R.id.m_fab);
        notifyText = NotificationText.goal(mentee.getUcid());

        CHECKMARK_1 = view.findViewById(R.id.checkmark1);
        CHECKMARK_2 = view.findViewById(R.id.checkmark2);
        CHECKMARK_3 = view.findViewById(R.id.checkmark3);
        CHECKMARK_4 = view.findViewById(R.id.checkmark4);

        CHECKMARK_1.setOnClickListener(this);
        CHECKMARK_2.setOnClickListener(this);
        CHECKMARK_3.setOnClickListener(this);
        CHECKMARK_4.setOnClickListener(this);

        no_goals = view.findViewById(R.id.no_goals);
        GOAL_1 = view.findViewById(R.id.goal1);
        GOAL_2 = view.findViewById(R.id.goal2);
        GOAL_3 = view.findViewById(R.id.goal3);
        GOAL_4 = view.findViewById(R.id.goal4);
        SEMESTER = view.findViewById(R.id.semester);
        WEEKS = view.findViewById(R.id.weeks);
        ACCOUNT = view.findViewById(R.id.account);
        EMAIL = view.findViewById(R.id.sendEmail);
        PERCENT = view.findViewById(R.id.percent);
        goals_layout = view.findViewById(R.id.goals);
        ACCOUNT.setOnClickListener(this);
        EMAIL.setOnClickListener(this);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        changeSemesterYear(SEMESTER);
        RequestQueue queue = Volley.newRequestQueue(view.getContext());
        StringRequest stringRequest_2 = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("DEBUG_OUTPUT","Server Response: "+response);
                        if (response.equals("empty")) {
                            String no_goal = "No new goals from " + mentor.getFname();
                            GOAL_1.setText(no_goal);
                            GOAL_2.setText(no_goal);
                            GOAL_3.setText(no_goal);
                            GOAL_4.setText(no_goal);
                            goals_layout.setVisibility(View.GONE);
                            no_goals.setVisibility(View.VISIBLE);
                            CHECKMARK_1.setEnabled(false);
                            CHECKMARK_2.setEnabled(false);
                            CHECKMARK_3.setEnabled(false);
                            CHECKMARK_4.setEnabled(false);
                        } else {
                            no_goals.setVisibility(View.GONE);
                            goals_layout.setVisibility(View.VISIBLE);
                            String[] goals = response.split("\\|");
                            c1 = loadGoal(goals[0], GOAL_1, CHECKMARK_1);
                            c2 = loadGoal(goals[1], GOAL_2, CHECKMARK_2);
                            c3 = loadGoal(goals[2], GOAL_3, CHECKMARK_3);
                            c4 = loadGoal(goals[3], GOAL_4, CHECKMARK_4);
                            Log.d("DEBUG_OUTPUT","Server Response: "+Arrays.toString(goals));
                            percentageComplete(PERCENT);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                Log.d("DEBUG_OUTPUT","Volley Error: "+error);
                error.printStackTrace();
                if(error instanceof TimeoutError)
                    Toast.makeText(
                            view.getContext(),
                            "Request timed out. Check your network settings.",
                            Toast.LENGTH_SHORT
                    ).show();
                else if(error instanceof NetworkError)
                    Toast.makeText(
                            view.getContext(),
                            "Can't connect to the internet",
                            Toast.LENGTH_SHORT
                    ).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("action", "getGoals");
                params.put("ucid", mentee.getUcid());
                params.put("mentor", mentor.getUcid());
                return params;
            }
        };

        queue.add(stringRequest_2);
        stringRequest_2.setRetryPolicy(new DefaultRetryPolicy(
                3000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        );

        if (!isStudent(USER_TYPE)) {
            fab.show();
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), EditGoals.class);
                    intent.putExtra("goal1", GOAL_1.getText().toString());
                    intent.putExtra("goal2", GOAL_2.getText().toString());
                    intent.putExtra("goal3", GOAL_3.getText().toString());
                    intent.putExtra("goal4", GOAL_4.getText().toString());
                    startActivity(intent);
                }
            });
        }

        weeksLeft(SEMESTER);
    }

    @Override
    public void onClick(View v) {
        if (isStudent(USER_TYPE)) {
            switch (v.getId()) {
                case R.id.checkmark1:
                    if (!c1) {
                        CHECKMARK_1.setImageResource(R.drawable.ic_check_green);
                        c1 = true;
                        percentageComplete(PERCENT);
                        changeGoalStatus(
                                view,
                                url,
                                "changeGoalStatus",
                                mentee.getUcid(),
                                mentor.getUcid(),
                                GOAL_1.getText().toString(), "1"
                        );
                        PushMessageToFCM.send(getContext(), notifyText[0], notifyText[1]);
                        postToast("1");
                        break;
                    } else {
                        CHECKMARK_1.setImageResource(R.drawable.ic_check_circle);
                        c1 = false;
                        percentageComplete(PERCENT);
                        changeGoalStatus(
                                view,
                                url,
                                "changeGoalStatus",
                                mentee.getUcid(),
                                mentor.getUcid(),
                                GOAL_1.getText().toString(), "0"
                        );
                        postToast("0");
                        break;
                    }
                case R.id.checkmark2:
                    if (!c2) {
                        CHECKMARK_2.setImageResource(R.drawable.ic_check_green);
                        c2 = true;
                        percentageComplete(PERCENT);
                        changeGoalStatus(
                                view,
                                url,
                                "changeGoalStatus",
                                mentee.getUcid(),
                                mentor.getUcid(),
                                GOAL_2.getText().toString(), "1"
                        );
                        PushMessageToFCM.send(getContext(), notifyText[0], notifyText[1]);
                        postToast("1");
                        break;
                    } else {
                        CHECKMARK_2.setImageResource(R.drawable.ic_check_circle);
                        c2 = false;
                        percentageComplete(PERCENT);
                        changeGoalStatus(
                                view,
                                url,
                                "changeGoalStatus",
                                mentee.getUcid(),
                                mentor.getUcid(),
                                GOAL_2.getText().toString(), "0"
                        );
                        postToast("0");
                        break;
                    }

                case R.id.checkmark3:
                    if (!c3) {
                        CHECKMARK_3.setImageResource(R.drawable.ic_check_green);
                        c3 = true;
                        percentageComplete(PERCENT);
                        changeGoalStatus(
                                view,
                                url,
                                "changeGoalStatus",
                                mentee.getUcid(),
                                mentor.getUcid(),
                                GOAL_3.getText().toString(), "1"
                        );
                        PushMessageToFCM.send(getContext(), notifyText[0], notifyText[1]);
                        postToast("1");
                        break;
                    } else {
                        CHECKMARK_3.setImageResource(R.drawable.ic_check_circle);
                        c3 = false;
                        percentageComplete(PERCENT);
                        changeGoalStatus(
                                view,
                                url,
                                "changeGoalStatus",
                                mentee.getUcid(),
                                mentor.getUcid(),
                                GOAL_3.getText().toString(), "0"
                        );
                        postToast("0");
                        break;
                    }
                case R.id.checkmark4:
                    if (!c4) {
                        CHECKMARK_4.setImageResource(R.drawable.ic_check_green);
                        c4 = true;
                        percentageComplete(PERCENT);
                        changeGoalStatus(
                                view,
                                url,
                                "changeGoalStatus",
                                mentee.getUcid(),
                                mentor.getUcid(),
                                GOAL_4.getText().toString(), "1"
                        );
                        PushMessageToFCM.send(getContext(), notifyText[0], notifyText[1]);
                        postToast("1");
                        break;
                    } else {
                        CHECKMARK_4.setImageResource(R.drawable.ic_check_circle);
                        c4 = false;
                        percentageComplete(PERCENT);
                        changeGoalStatus(
                                view,
                                url,
                                "changeGoalStatus",
                                mentee.getUcid(),
                                mentor.getUcid(),
                                GOAL_4.getText().toString(), "0"
                        );
                        postToast("0");
                        break;
                    }

                default:
                    break;
            }
        }

        switch (v.getId())
        {
            case R.id.account:
                startActivity(new Intent(getContext(), MentorActivity.class));
                break;

            case R.id.sendEmail:
                startActivity(new Intent(getContext(), SendEmail.class));
                break;

            default:
                break;
        }
    }

    /* Display the goal and check mark status */
    private boolean loadGoal(String goal, TextView tv_goal, ImageView ck)
    {
        boolean s = false;
        String [] data = goal.split("\\\\");
        tv_goal.setText(data[0]);

        switch(data[1])
        {
            case "1":
                s = true;
                tv_goal.setVisibility(View.VISIBLE);
                ck.setVisibility(View.VISIBLE);
                ck.setImageResource(R.drawable.ic_check_green);
                ck.setEnabled(true);
                break;

            case "0":
                s = false;
                tv_goal.setVisibility(View.VISIBLE);
                ck.setVisibility(View.VISIBLE);
                ck.setImageResource(R.drawable.ic_check_circle);
                ck.setEnabled(true);
                break;

            case "3":
                s = false;
                tv_goal.setVisibility(View.INVISIBLE);
                ck.setVisibility(View.INVISIBLE);
                break;

            default:
                break;
        }
        return s;
    }

    /* Send a server request to update the goal status (complete/incomplete)  */
    private void changeGoalStatus(View v, String url, final String action,
                                  final String currentUser, final String otherUser, final String goal, final String status)
    {
        RequestQueue queue = Volley.newRequestQueue(v.getContext());
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
                            view.getContext(),
                            "Request timed out. Check your network settings.",
                            Toast.LENGTH_SHORT
                    ).show();
                else if(error instanceof NetworkError)
                    Toast.makeText(
                            view.getContext(),
                            "Can't connect to the internet",
                            Toast.LENGTH_SHORT
                    ).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("action", action);
                params.put("currentUser", currentUser);
                params.put("otherUser", otherUser);
                params.put("goal", goal);
                params.put("status", status);
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

    /* Post a toast message after toggling the check marks */
    private void postToast(String status)
    {
        if (status.equals("0"))
        {
            Context context = getContext();
            CharSequence text = "Goal Incomplete";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }
        else if (status.equals("1"))
        {
            Context context = getContext();
            CharSequence text = "Goal Complete";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }

    }

    /* Adjust the year and semester given the current time of year */
    private void changeSemesterYear(TextView semester)
    {
        Calendar cal = Calendar.getInstance();
        int month = cal.get(Calendar.MONTH);
        String year = Integer.toString(cal.get(Calendar.YEAR));
        Log.d("DEBUG_OUTPUT","Current Year: "+year);

        if (month > 7)
        {
            String sem = "Fall " + year;
            semester.setText(sem);
        }
        else if (month < 5)
        {
            String sem = "Spring " + year;
            semester.setText(sem);
        }
        else if (4 < month || month < 8)
        {
            String sem = "Summer " + year;
            semester.setText(sem);
        }
    }

    /* Verify the user type who's viewing this page */
    private boolean isStudent(SharedPreferences type)
    {
        return type.getString("type", null).equals("student");
    }

    /* Set the percentage of goals completed, update real-time as user updates their status */
    private void percentageComplete(TextView percent)
    {
        boolean [] checks = {c1, c2, c3, c4};
        int p = 0;

        for(boolean check : checks)
        {
            if(check)
            {
                p += 25;
            }
        }
        String per = Integer.toString(p);
        String percent_text = " " + per + " %";
        percent.setText(percent_text);
    }

    /* Set remaining amount of weeks left until the semester's conclusion */
    private void weeksLeft(TextView semester)
    {
        final String date = DateTimeFormat.lastWeekRemaining(semester);
        RequestQueue queue = Volley.newRequestQueue(view.getContext());
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("DEBUG_OUTPUT","Server Response: "+response);
                String weeks = response + " Weeks";
                WEEKS.setText(weeks);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("DEBUG_OUTPUT","Volley Error: "+error);
                error.printStackTrace();
                if(error instanceof TimeoutError)
                    Toast.makeText(
                            view.getContext(),
                            "Request timed out. Check your network settings.",
                            Toast.LENGTH_SHORT
                    ).show();
                else if(error instanceof NetworkError)
                    Toast.makeText(
                            view.getContext(),
                            "Can't connect to the internet",
                            Toast.LENGTH_SHORT
                    ).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("action", "weeksLeft");
                params.put("date", date);
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
}