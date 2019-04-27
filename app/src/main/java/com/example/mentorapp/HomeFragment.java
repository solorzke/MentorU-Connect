package com.example.mentorapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.mentorapp.model.JSON;
import com.example.mentorapp.model.VolleyCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class HomeFragment extends Fragment implements View.OnClickListener {

    /*
     * THIS FRAGMENT COVERS THE HOME SCREEN & IS THE DEFAULT SCREEN THAT SIDEBAR SENDS THE USER TO
     * AFTER LOGGING IN
     */

    SharedPreferences SESSION, MENTOR;
    SharedPreferences.Editor editor;
    ImageView CHECKMARK_1, CHECKMARK_2, CHECKMARK_3, CHECKMARK_4;
    TextView FEEDBACK, GOAL_1, GOAL_2, GOAL_3, GOAL_4, SEMESTER;
    String url = "https://web.njit.edu/~kas58/mentorDemo/query.php";
    Boolean c1 = false, c2 = false, c3 = false, c4 = false;
    View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);
        SESSION = getActivity().getSharedPreferences("USER", Context.MODE_PRIVATE);
        MENTOR = getActivity().getSharedPreferences("MENTOR", Context.MODE_PRIVATE);
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
        changeSemesterYear(SEMESTER);

        FEEDBACK = (TextView) view.findViewById(R.id.feedback);

        /* String Request to Receive Any Feedback from Mentor*/
        RequestQueue queue = Volley.newRequestQueue(view.getContext());
        StringRequest stringRequest_1 = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(response.equals("empty")) {
                            FEEDBACK.setText("No new feedback from " + MENTOR.getString("fname", null));
                        }
                        else{
                            FEEDBACK.setText(response);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error);
            }
        }){
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("action", "getFeedback");
                params.put("ucid", SESSION.getString("ucid", null));
                params.put("mentor", MENTOR.getString("ucid", null));  //Change this later <------

                return params;
            }
        };

        StringRequest stringRequest_2 = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println(response);
                        if(response.equals("empty")) {
                            System.out.println(response);
                            GOAL_1.setText("No new goals from " + MENTOR.getString("fname", null));
                            GOAL_2.setText("No new goals from " + MENTOR.getString("fname", null));
                            GOAL_3.setText("No new goals from " + MENTOR.getString("fname", null));
                            GOAL_4.setText("No new goals from " + MENTOR.getString("fname", null));
                            System.out.println(response);
                        }
                        else{
                            String[] goals = response.split("\\|");
                            loadGoal(goals[0], GOAL_1, CHECKMARK_1, c1);
                            loadGoal(goals[1], GOAL_2, CHECKMARK_2, c2);
                            loadGoal(goals[2], GOAL_3, CHECKMARK_3, c3);
                            loadGoal(goals[3], GOAL_4, CHECKMARK_4, c4);
                            System.out.println(Arrays.toString(goals));
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error);
            }
        }){
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("action", "getGoals");
                params.put("ucid", SESSION.getString("ucid", null));
                params.put("mentor", MENTOR.getString("ucid", null));
                return params;
            }
        };


        queue.add(stringRequest_1);
        queue.add(stringRequest_2);


        return view;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.checkmark1:
                if(!c1){
                    CHECKMARK_1.setImageResource(R.drawable.ic_check_green);
                    c1 = true;
                    changeGoalStatus(view, url, "changeGoalStatus", SESSION.getString("ucid", null),
                            MENTOR.getString("ucid", null), GOAL_1.getText().toString(), "1");
                    postToast("1");
                    break;
                }
                else{
                    CHECKMARK_1.setImageResource(R.drawable.ic_check_circle);
                    c1 = false;
                    changeGoalStatus(view, url, "changeGoalStatus", SESSION.getString("ucid", null),
                            MENTOR.getString("ucid", null), GOAL_1.getText().toString(), "0");
                    postToast("0");
                    break;
                }
            case R.id.checkmark2:
                if(!c2){
                    CHECKMARK_2.setImageResource(R.drawable.ic_check_green);
                    c2 = true;
                    changeGoalStatus(view, url, "changeGoalStatus", SESSION.getString("ucid", null),
                            MENTOR.getString("ucid", null), GOAL_2.getText().toString(), "1");
                    postToast("1");
                    break;
                }
                else{
                    CHECKMARK_2.setImageResource(R.drawable.ic_check_circle);
                    c2 = false;
                    changeGoalStatus(view, url, "changeGoalStatus", SESSION.getString("ucid", null),
                            MENTOR.getString("ucid", null), GOAL_2.getText().toString(), "0");
                    postToast("0");
                    break;
                }

            case R.id.checkmark3:
                if(!c3){
                    CHECKMARK_3.setImageResource(R.drawable.ic_check_green);
                    c3 = true;
                    changeGoalStatus(view, url, "changeGoalStatus", SESSION.getString("ucid", null),
                            MENTOR.getString("ucid", null), GOAL_3.getText().toString(), "1");
                    postToast("1");
                    break;
                }
                else{
                    CHECKMARK_3.setImageResource(R.drawable.ic_check_circle);
                    c3 = false;
                    changeGoalStatus(view, url, "changeGoalStatus", SESSION.getString("ucid", null),
                            MENTOR.getString("ucid", null), GOAL_3.getText().toString(), "0");
                    postToast("0");
                    break;
                }
            case R.id.checkmark4:
                if(!c4){
                    CHECKMARK_4.setImageResource(R.drawable.ic_check_green);
                    c4 = true;
                    changeGoalStatus(view, url, "changeGoalStatus", SESSION.getString("ucid", null),
                            MENTOR.getString("ucid", null), GOAL_4.getText().toString(), "1");
                    postToast("1");
                    break;
                }
                else{
                    CHECKMARK_4.setImageResource(R.drawable.ic_check_circle);
                    c4 = false;
                    changeGoalStatus(view, url, "changeGoalStatus", SESSION.getString("ucid", null),
                            MENTOR.getString("ucid", null), GOAL_4.getText().toString(), "0");
                    postToast("0");
                    break;
                }
            default:
                break;
        }
    }

    private void loadGoal(String goal, TextView tv_goal, ImageView ck, boolean sentinel){

        String [] data = goal.split("\\\\");
        tv_goal.setText(data[0]);
        if(data[1].equals("1")){
            sentinel = true;
            ck.setImageResource(R.drawable.ic_check_green);
        }
        else if(data[1].equals("0")){
            sentinel = false;
            ck.setImageResource(R.drawable.ic_check_circle);
        }
    }

    private void changeGoalStatus(View v, String url, final String action,
                                final String currentUser, final String otherUser, final String goal, final String status){

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

    private void postToast(String status){

        if(status.equals("0")){
            Context context = getContext();
            CharSequence text = "Goal Incomplete";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }
        else if(status.equals("1")){
            Context context = getContext();
            CharSequence text = "Goal Complete";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }

    }

    private void changeSemesterYear(TextView semester){
        Calendar cal = Calendar.getInstance();
        int month = cal.get(Calendar.MONTH);
        String year = Integer.toString(cal.get(Calendar.YEAR));
        System.out.println(year);

        if(month > 7){
            semester.setText("Fall '" + year);
        }

        else if(month < 5){
            semester.setText("Summer '" + year);
        }

        else if(4 < month || month < 8){
            semester.setText("Summer '" + year);
        }
    }
}
