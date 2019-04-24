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
import android.widget.ImageView;
import android.widget.TextView;

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
    TextView FEEDBACK, GOAL_1, GOAL_2, GOAL_3, GOAL_4;
    Boolean c1 = false, c2 = false, c3 = false, c4 = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
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

        FEEDBACK = (TextView) view.findViewById(R.id.feedback);

        /* String Request to Receive Any Feedback from Mentor*/
        RequestQueue queue = Volley.newRequestQueue(view.getContext());
        String url = "https://web.njit.edu/~kas58/mentorDemo/query.php";
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
                            GOAL_1.setText(goals[0]);
                            GOAL_2.setText(goals[1]);
                            GOAL_3.setText(goals[2]);
                            GOAL_4.setText(goals[3]);
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
                    break;
                }
                else{
                    CHECKMARK_1.setImageResource(R.drawable.ic_check_circle);
                    c1 = false;
                    break;
                }
            case R.id.checkmark2:
                if(!c2){
                    CHECKMARK_2.setImageResource(R.drawable.ic_check_green);
                    c2 = true;
                    break;
                }
                else{
                    CHECKMARK_2.setImageResource(R.drawable.ic_check_circle);
                    c2 = false;
                    break;
                }

            case R.id.checkmark3:
                if(!c3){
                    CHECKMARK_3.setImageResource(R.drawable.ic_check_green);
                    c3 = true;
                    break;
                }
                else{
                    CHECKMARK_3.setImageResource(R.drawable.ic_check_circle);
                    c3 = false;
                    break;
                }
            case R.id.checkmark4:
                if(!c4){
                    CHECKMARK_4.setImageResource(R.drawable.ic_check_green);
                    c4 = true;
                    break;
                }
                else{
                    CHECKMARK_4.setImageResource(R.drawable.ic_check_circle);
                    c4 = false;
                    break;
                }
            default:
                break;
        }
    }

}
