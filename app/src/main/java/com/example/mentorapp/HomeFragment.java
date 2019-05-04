package com.example.mentorapp;

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
import com.example.mentorapp.FAB.EditGoals;
import com.example.mentorapp.FAB.EditMessage;

import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class HomeFragment extends Fragment implements View.OnClickListener {

    /*
     * THIS FRAGMENT COVERS THE HOME SCREEN & IS THE DEFAULT SCREEN THAT SIDEBAR SENDS THE USER TO
     * AFTER LOGGING IN
     */

    SharedPreferences STUDENT, MENTOR, USER_TYPE;
    SharedPreferences.Editor editor;
    ImageView CHECKMARK_1, CHECKMARK_2, CHECKMARK_3, CHECKMARK_4;
    FloatingActionButton fab;
    TextView FEEDBACK, GOAL_1, GOAL_2, GOAL_3, GOAL_4, SEMESTER, GOALS, M_TITLE;
    String url = "https://web.njit.edu/~kas58/mentorDemo/query.php";
    AlertDialog dialog;
    Boolean c1 = false, c2 = false, c3 = false, c4 = false;
    View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);
        STUDENT = getActivity().getSharedPreferences("STUDENT", Context.MODE_PRIVATE);
        MENTOR = getActivity().getSharedPreferences("MENTOR", Context.MODE_PRIVATE);
        USER_TYPE = getActivity().getSharedPreferences("USER_TYPE", Context.MODE_PRIVATE);
        fab = view.findViewById(R.id.m_fab);

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
        GOALS = view.findViewById(R.id.goals);

        SEMESTER = view.findViewById(R.id.semester);
        FEEDBACK = (TextView) view.findViewById(R.id.feedback);
        M_TITLE = view.findViewById(R.id.textView7);

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
        /* String Request to Receive Any Feedback from Mentor*/
        RequestQueue queue = Volley.newRequestQueue(view.getContext());
        StringRequest stringRequest_1 = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.equals("empty")) {
                            FEEDBACK.setText("No new feedback from " + MENTOR.getString("fname", null));
                        } else {
                            FEEDBACK.setText(response);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("action", "getFeedback");
                params.put("ucid", STUDENT.getString("ucid", null));
                params.put("mentor", MENTOR.getString("ucid", null));  //Change this later <------

                return params;
            }
        };

        StringRequest stringRequest_2 = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println(response);
                        if (response.equals("empty")) {
                            System.out.println(response);
                            GOAL_1.setText("No new goals from " + MENTOR.getString("fname", null));
                            GOAL_2.setText("No new goals from " + MENTOR.getString("fname", null));
                            GOAL_3.setText("No new goals from " + MENTOR.getString("fname", null));
                            GOAL_4.setText("No new goals from " + MENTOR.getString("fname", null));
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
                            System.out.println(Arrays.toString(goals));
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("action", "getGoals");
                params.put("ucid", STUDENT.getString("ucid", null));
                params.put("mentor", MENTOR.getString("ucid", null));
                return params;
            }
        };

        queue.add(stringRequest_1);
        queue.add(stringRequest_2);

        if (!isStudent(USER_TYPE)) {
            fab.show();
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.show();
                }
            });
            GOALS.setText("Your Mentee's Goals");
            M_TITLE.setText("Your message to Mentee:");
        }
    }

    @Override
    public void onClick(View v) {
        if (isStudent(USER_TYPE)) {
            switch (v.getId()) {
                case R.id.checkmark1:
                    if (!c1) {
                        CHECKMARK_1.setImageResource(R.drawable.ic_check_green);
                        c1 = true;
                        changeGoalStatus(view, url, "changeGoalStatus", STUDENT.getString("ucid", null),
                                MENTOR.getString("ucid", null), GOAL_1.getText().toString(), "1");
                        postToast("1");
                        break;
                    } else {
                        CHECKMARK_1.setImageResource(R.drawable.ic_check_circle);
                        c1 = false;
                        changeGoalStatus(view, url, "changeGoalStatus", STUDENT.getString("ucid", null),
                                MENTOR.getString("ucid", null), GOAL_1.getText().toString(), "0");
                        postToast("0");
                        break;
                    }
                case R.id.checkmark2:
                    if (!c2) {
                        CHECKMARK_2.setImageResource(R.drawable.ic_check_green);
                        c2 = true;
                        changeGoalStatus(view, url, "changeGoalStatus", STUDENT.getString("ucid", null),
                                MENTOR.getString("ucid", null), GOAL_2.getText().toString(), "1");
                        postToast("1");
                        break;
                    } else {
                        CHECKMARK_2.setImageResource(R.drawable.ic_check_circle);
                        c2 = false;
                        changeGoalStatus(view, url, "changeGoalStatus", STUDENT.getString("ucid", null),
                                MENTOR.getString("ucid", null), GOAL_2.getText().toString(), "0");
                        postToast("0");
                        break;
                    }

                case R.id.checkmark3:
                    if (!c3) {
                        CHECKMARK_3.setImageResource(R.drawable.ic_check_green);
                        c3 = true;
                        changeGoalStatus(view, url, "changeGoalStatus", STUDENT.getString("ucid", null),
                                MENTOR.getString("ucid", null), GOAL_3.getText().toString(), "1");
                        postToast("1");
                        break;
                    } else {
                        CHECKMARK_3.setImageResource(R.drawable.ic_check_circle);
                        c3 = false;
                        changeGoalStatus(view, url, "changeGoalStatus", STUDENT.getString("ucid", null),
                                MENTOR.getString("ucid", null), GOAL_3.getText().toString(), "0");
                        postToast("0");
                        break;
                    }
                case R.id.checkmark4:
                    if (!c4) {
                        CHECKMARK_4.setImageResource(R.drawable.ic_check_green);
                        c4 = true;
                        changeGoalStatus(view, url, "changeGoalStatus", STUDENT.getString("ucid", null),
                                MENTOR.getString("ucid", null), GOAL_4.getText().toString(), "1");
                        postToast("1");
                        break;
                    } else {
                        CHECKMARK_4.setImageResource(R.drawable.ic_check_circle);
                        c4 = false;
                        changeGoalStatus(view, url, "changeGoalStatus", STUDENT.getString("ucid", null),
                                MENTOR.getString("ucid", null), GOAL_4.getText().toString(), "0");
                        postToast("0");
                        break;
                    }
                default:
                    break;
            }
        } else {

        }
    }

    private boolean loadGoal(String goal, TextView tv_goal, ImageView ck) {

        boolean s = false;
        String[] data = goal.split("\\\\");
        tv_goal.setText(data[0]);
        if (data[1].equals("1")) {
            s = true;
            tv_goal.setVisibility(View.VISIBLE);
            ck.setVisibility(View.VISIBLE);
            ck.setImageResource(R.drawable.ic_check_green);
            ck.setEnabled(true);
        } else if (data[1].equals("0")) {
            s = false;
            tv_goal.setVisibility(View.VISIBLE);
            ck.setVisibility(View.VISIBLE);
            ck.setImageResource(R.drawable.ic_check_circle);
            ck.setEnabled(true);
        } else if (data[1].equals("3")) {
            s = false;
            tv_goal.setVisibility(View.INVISIBLE);
            ck.setVisibility(View.INVISIBLE);
        }

        return s;
    }

    private void changeGoalStatus(View v, String url, final String action,
                                  final String currentUser, final String otherUser, final String goal, final String status) {

        RequestQueue queue = Volley.newRequestQueue(v.getContext());
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error);
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

    private void postToast(String status) {

        if (status.equals("0")) {
            Context context = getContext();
            CharSequence text = "Goal Incomplete";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        } else if (status.equals("1")) {
            Context context = getContext();
            CharSequence text = "Goal Complete";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }

    }

    private void changeSemesterYear(TextView semester) {
        Calendar cal = Calendar.getInstance();
        int month = cal.get(Calendar.MONTH);
        String year = Integer.toString(cal.get(Calendar.YEAR));
        System.out.println(year);

        if (month > 7) {
            semester.setText("Fall '" + year);
        } else if (month < 5) {
            semester.setText("Spring '" + year);
        } else if (4 < month || month < 8) {
            semester.setText("Summer '" + year);
        }
    }

    private boolean isStudent(SharedPreferences type) {
        if (type.getString("type", null).equals("student")) {
            return true;
        } else {
            return false;
        }
    }
}
