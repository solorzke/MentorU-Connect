package com.example.mentorapp.model;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.mentorapp.R;
import com.example.mentorapp.SMFragment;
import com.example.mentorapp.SendEmail;
import com.example.mentorapp.SideBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class JSON extends AppCompatActivity {

    /*
    THIS CLASS IS TO SEND/RECEIVE A JSON OBJECT REGARDING THE USER'S INFORMATION TO SAVE AS A
    SESSION IN THE APP. THEREFORE, THERE IS NO XML/UI TIED TO THIS CLASS.
     */

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_json);

        final String ucid = getIntent().getExtras().getString("com.example.mentorapp.UCID");
        String confirm = getIntent().getExtras().getString(
                "com.example.mentorapp.CONFIRM");

        /*1. START: SENDING A REQUEST TO RECEIVE A JSON ARRAY OF THE USER'S RECORD *ONLY*
         * AFTER LOGGING IN */
        String url = "https://web.njit.edu/~kas58/mentorDemo/query.php";

        if(confirm.equals("student")){

            SharedPreferences.Editor user_type = getSharedPreferences("USER_TYPE",
                    Context.MODE_PRIVATE).edit();
            user_type.clear();
            user_type.putString("type", "student");
            user_type.apply();

            RequestQueue rq = Volley.newRequestQueue(JSON.this);

            Map<String, String> params = new HashMap<String, String>();
            params.put("signedIn", "true");
            params.put("asWho", "student");
            params.put("UCID", ucid);
            JSONObject parameters = new JSONObject(params);

            JsonObjectRequest jReq = new JsonObjectRequest(Request.Method.POST, url, parameters, new
                    Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try{

                                System.out.println(response);
                                JSONArray array = response.getJSONArray("record");
                                JSONArray courses = response.getJSONArray("courses");
                                JSONArray m_record = response.getJSONArray("record_m");

                                // Store user data into SESSION
                                JSONObject student = array.getJSONObject(0);
                                SharedPreferences.Editor editor = getSharedPreferences("STUDENT",
                                        Context.MODE_PRIVATE).edit();
                                editor.clear();
                                editor.putString("ucid", student.getString("ucid"));
                                editor.putString("fname", student.getString("fname"));
                                editor.putString("lname", student.getString("lname"));
                                editor.putString("email", student.getString("email"));
                                editor.putString("degree", student.getString("degree"));
                                editor.putString("club", student.getString("club"));
                                editor.putString("avi", student.getString("avi"));
                                editor.putString("firstEntry", "true");
                                editor.apply();

                                /* Store Student Schedule Preferences */
                                SharedPreferences.Editor courseList = getSharedPreferences("COURSES",
                                        Context.MODE_PRIVATE).edit();
                                courseList.clear();

                                for(int i = 0; i < courses.length(); i++){
                                    JSONObject course = courses.getJSONObject(i);
                                    courseList.putString("row_id"+Integer.toString(i), course.getString("id"));
                                    courseList.putString("id"+Integer.toString(i), course.getString("course_num"));
                                    courseList.putString("title"+Integer.toString(i), course.getString("course_title"));
                                }

                                courseList.apply();

                                /* Store Mentor Record Data */
                                SharedPreferences.Editor MENTOR = getSharedPreferences("MENTOR", Context.MODE_PRIVATE).edit();
                                MENTOR.clear();
                                JSONObject mentor = m_record.getJSONObject(0);
                                MENTOR.putString("ucid", mentor.getString("ucid"));
                                MENTOR.putString("fname", mentor.getString("fname"));
                                MENTOR.putString("lname", mentor.getString("lname"));
                                MENTOR.putString("email", mentor.getString("email"));
                                MENTOR.putString("grad_date", mentor.getString("grad_date"));
                                MENTOR.putString("occupation", mentor.getString("occupation"));
                                MENTOR.putString("degree", mentor.getString("degree"));
                                MENTOR.putString("mentee", mentor.getString("mentee"));
                                MENTOR.putString("avi", mentor.getString("avi"));
                                MENTOR.apply();

                                startActivity(new Intent(getApplicationContext(), SideBar.class));
                                finish();

                            }catch (JSONException e){
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    System.out.println(error);
                }
            });

            rq.add(jReq);
        }
        /* 1. END */

        else if(confirm.equals("mentor"))
        {
            SharedPreferences.Editor user_type = getSharedPreferences("USER_TYPE",
                    Context.MODE_PRIVATE).edit();
            user_type.clear();
            user_type.putString("type", "mentor");
            user_type.apply();
            loadAsMentor(url, ucid);
        }

    }

    @Override
    public void onBackPressed() {
        /* CANNOT GO BACK TO LOGIN HERE*/
    }

    private void loadAsMentor(String url, String ucid)
    {
        RequestQueue rq = Volley.newRequestQueue(JSON.this);

        Map<String, String> params = new HashMap<String, String>();
        params.put("signedIn", "true");
        params.put("asWho", "mentor");
        params.put("UCID", ucid);
        JSONObject parameters = new JSONObject(params);

        JsonObjectRequest jReq = new JsonObjectRequest(Request.Method.POST, url, parameters, new
                Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try{

                            System.out.println(response);
                            JSONArray array = response.getJSONArray("record");
                            JSONArray courses = response.getJSONArray("courses");
                            JSONArray m_record = response.getJSONArray("record_m");

                            // Store student data into SESSION
                            JSONObject student = array.getJSONObject(0);
                            SharedPreferences.Editor editor = getSharedPreferences("STUDENT",
                                    Context.MODE_PRIVATE).edit();
                            editor.clear();
                            editor.putString("ucid", student.getString("ucid"));
                            editor.putString("fname", student.getString("fname"));
                            editor.putString("lname", student.getString("lname"));
                            editor.putString("email", student.getString("email"));
                            editor.putString("degree", student.getString("degree"));
                            editor.putString("club", student.getString("club"));
                            editor.putString("avi", student.getString("avi"));
                            editor.putString("firstEntry", "true");
                            editor.apply();

                            /* Store Student Schedule Preferences */
                            SharedPreferences.Editor courseList = getSharedPreferences("COURSES",
                                    Context.MODE_PRIVATE).edit();
                            courseList.clear();

                            for(int i = 0; i < courses.length(); i++){
                                JSONObject course = courses.getJSONObject(i);
                                courseList.putString("row_id"+Integer.toString(i), course.getString("id"));
                                courseList.putString("id"+Integer.toString(i), course.getString("course_num"));
                                courseList.putString("title"+Integer.toString(i), course.getString("course_title"));
                            }

                            courseList.apply();

                            /* Store Mentor Record Data */
                            SharedPreferences.Editor MENTOR = getSharedPreferences("MENTOR", Context.MODE_PRIVATE).edit();
                            MENTOR.clear();
                            JSONObject mentor = m_record.getJSONObject(0);
                            MENTOR.putString("ucid", mentor.getString("ucid"));
                            MENTOR.putString("fname", mentor.getString("fname"));
                            MENTOR.putString("lname", mentor.getString("lname"));
                            MENTOR.putString("email", mentor.getString("email"));
                            MENTOR.putString("grad_date", mentor.getString("grad_date"));
                            MENTOR.putString("occupation", mentor.getString("occupation"));
                            MENTOR.putString("degree", mentor.getString("degree"));
                            MENTOR.putString("mentee", mentor.getString("mentee"));
                            MENTOR.putString("avi", mentor.getString("avi"));
                            MENTOR.putString("firstEntry", "true");
                            MENTOR.apply();

                            startActivity(new Intent(getApplicationContext(), SideBar.class));
                            finish();

                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error);
            }
        });

        rq.add(jReq);
    }


}