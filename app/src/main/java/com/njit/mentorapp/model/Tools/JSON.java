/*
 * This class is to send/receive JSON objects regarding the user's information to save as a
 * session in the application. This information is only used as easier access to user's information
 * rather than sending Volley requests in every other class that'll need it.
 */

package com.njit.mentorapp.model.Tools;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.njit.mentorapp.Login;
import com.njit.mentorapp.R;
import com.njit.mentorapp.SideBar;
import com.njit.mentorapp.model.Service.MySingleton;
import com.njit.mentorapp.model.Service.WebServer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class JSON extends AppCompatActivity
{
    private String ucid, confirm;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_json);
        ucid = getIntent().getExtras().getString("com.example.mentorapp.UCID");
        confirm = getIntent().getExtras().getString("com.example.mentorapp.CONFIRM");
        defineUserType(confirm);
        loadUser(confirm);
    }

    @Override
    public void onBackPressed() {
        /* CANNOT GO BACK TO LOGIN HERE*/
    }

    private void defineUserType(String type)
    {
        if(type.equals("student"))
        {
            SharedPreferences.Editor user_type = getSharedPreferences("USER_TYPE",
                    Context.MODE_PRIVATE).edit();
            user_type.clear();
            user_type.putString("type", "student");
            user_type.apply();
        }
        else
        {
            SharedPreferences.Editor user_type = getSharedPreferences("USER_TYPE",
                    Context.MODE_PRIVATE).edit();
            user_type.clear();
            user_type.putString("type", "mentor");
            user_type.apply();
        }
    }

    private void loadUser(final String type)
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put("signedIn", "true");
        params.put("asWho", type);
        params.put("UCID", ucid);
        JSONObject parameters = new JSONObject(params);

        JsonObjectRequest jReq = new JsonObjectRequest(
                Request.Method.POST,
                WebServer.getQueryLink(),
                parameters,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        try{

                            System.out.println("Server Response: "+response);
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
                            editor.putString("age", student.getString("age"));
                            editor.putString("birthday", student.getString("birthday"));
                            editor.putString("grade", student.getString("grade"));
                            editor.putString("grad_date", student.getString("grad_date"));
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
                            MENTOR.putString("age", mentor.getString("age"));
                            MENTOR.putString("birthday", mentor.getString("birthday"));
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
                System.out.println("Volley Error: "+error);
            }
        });

        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(jReq);

        jReq.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 50000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError
            {
                error.printStackTrace();
                AlertDialog alert = new AlertDialog.Builder(JSON.this).create();
                alert.setTitle("Timeout Period Ran Out");
                alert.setMessage("Request timed out. Heading back to Login page.");
                alert.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(getApplicationContext(), Login.class));
                        finish();
                    }
                });
            }
        });
    }
}