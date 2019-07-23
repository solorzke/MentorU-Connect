package com.njit.mentorapp.home;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.njit.mentorapp.account.MentorActivity;
import com.njit.mentorapp.fab.EditGoals;
import com.njit.mentorapp.fab.EditMessage;
import com.njit.mentorapp.model.service.NotificationText;
import com.njit.mentorapp.model.service.PushMessageToFCM;
import com.njit.mentorapp.R;
import com.njit.mentorapp.SendEmail;
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
    TextView GOAL_1, GOAL_2, GOAL_3, GOAL_4, SEMESTER, GOALS, M_TITLE, WEEKS, ACCOUNT, EMAIL, PERCENT;
    String url = WebServer.getQueryLink();
    String [] notifyText;
    AlertDialog dialog;
    Boolean c1 = false, c2 = false, c3 = false, c4 = false;
    View view;
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

        CHECKMARK_1 = (ImageView) view.findViewById(R.id.checkmark1);
        CHECKMARK_2 = (ImageView) view.findViewById(R.id.checkmark2);
        CHECKMARK_3 = (ImageView) view.findViewById(R.id.checkmark3);
        CHECKMARK_4 = (ImageView) view.findViewById(R.id.checkmark4);

        CHECKMARK_1.setOnClickListener(this);
        CHECKMARK_2.setOnClickListener(this);
        CHECKMARK_3.setOnClickListener(this);
        CHECKMARK_4.setOnClickListener(this);

        GOAL_1 = view.findViewById(R.id.goal1);
        GOAL_2 = view.findViewById(R.id.goal2);
        GOAL_3 = view.findViewById(R.id.goal3);
        GOAL_4 = view.findViewById(R.id.goal4);
        SEMESTER = view.findViewById(R.id.semester);
        WEEKS = view.findViewById(R.id.weeks);
        ACCOUNT = view.findViewById(R.id.account);
        EMAIL = view.findViewById(R.id.sendEmail);
        PERCENT = view.findViewById(R.id.percent);

        ACCOUNT.setOnClickListener(this);
        EMAIL.setOnClickListener(this);

        dialog = new AlertDialog.Builder(getContext()).create();
        dialog.setTitle("Choose Option");
        dialog.setMessage("Select option to add/edit");
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, "Goals", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent intent = new Intent(getContext(), EditGoals.class);
                intent.putExtra("goal1", GOAL_1.getText().toString());
                intent.putExtra("goal2", GOAL_2.getText().toString());
                intent.putExtra("goal3", GOAL_3.getText().toString());
                intent.putExtra("goal4", GOAL_4.getText().toString());
                startActivity(intent);
            }
        });
        dialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Message", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                startActivity(new Intent(getContext(), EditMessage.class));
            }
        });
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
                            GOAL_1.setText("No new goals from " + mentor.getFname());
                            GOAL_2.setText("No new goals from " + mentor.getFname());
                            GOAL_3.setText("No new goals from " + mentor.getFname());
                            GOAL_4.setText("No new goals from " + mentor.getFname());
                            CHECKMARK_1.setEnabled(false);
                            CHECKMARK_2.setEnabled(false);
                            CHECKMARK_3.setEnabled(false);
                            CHECKMARK_4.setEnabled(false);
                        } else {
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
            public void onErrorResponse(VolleyError error) {
                Log.d("DEBUG_OUTPUT","Volley Error: "+error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("action", "getGoals");
                params.put("ucid", mentee.getUcid());
                params.put("mentor", mentor.getUcid());
                return params;
            }
        };

        queue.add(stringRequest_2);

        if (!isStudent(USER_TYPE)) {
            fab.show();
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.show();
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
    }

    /* Display the goal and check mark status */
    private boolean loadGoal(String goal, TextView tv_goal, ImageView ck)
    {
        boolean s = false;
        String[] data = goal.split("\\\\");
        tv_goal.setText(data[0]);
        if (data[1].equals("1"))
        {
            s = true;
            tv_goal.setVisibility(View.VISIBLE);
            ck.setVisibility(View.VISIBLE);
            ck.setImageResource(R.drawable.ic_check_green);
            ck.setEnabled(true);
        }
        else if (data[1].equals("0"))
        {
            s = false;
            tv_goal.setVisibility(View.VISIBLE);
            ck.setVisibility(View.VISIBLE);
            ck.setImageResource(R.drawable.ic_check_circle);
            ck.setEnabled(true);
        }
        else if (data[1].equals("3"))
        {
            s = false;
            tv_goal.setVisibility(View.INVISIBLE);
            ck.setVisibility(View.INVISIBLE);
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
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("action", action);
                params.put("currentUser", currentUser);
                params.put("otherUser", otherUser);
                params.put("goal", goal);
                params.put("status", status);
                return params;
            }
        };
        queue.add(request);
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
            semester.setText("Fall " + year);
        }
        else if (month < 5)
        {
            semester.setText("Spring " + year);
        }
        else if (4 < month || month < 8)
        {
            semester.setText("Summer " + year);
        }
    }

    /* Verify the user type who's viewing this page */
    private boolean isStudent(SharedPreferences type)
    {
        if (type.getString("type", null).equals("student"))
        {
            return true;
        }
        else {
            return false;
        }
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
        percent.setText(" " + per + " %");
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
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("action", "weeksLeft");
                params.put("date", date);
                return params;
            }
        };
        queue.add(request);

    }
}

