/*
 * This class is to send/receive JSON objects regarding the user's information to save as a
 * session in the application. This information is only used as easier access to user's information
 * rather than sending Volley requests in every other class that'll need it.
 */

package com.njit.mentorapp.model.tools;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.njit.mentorapp.R;
import com.njit.mentorapp.model.service.FireBaseServer;
import com.njit.mentorapp.model.users.Mentee;
import com.njit.mentorapp.model.users.Mentor;
import com.njit.mentorapp.model.users.ReceivingUser;
import com.njit.mentorapp.model.users.UserType;
import com.njit.mentorapp.sidebar.SideBar;
import com.njit.mentorapp.model.service.MySingleton;
import com.njit.mentorapp.model.service.WebServer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class JSON extends AppCompatActivity
{
    private String ucid;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_json);
        ucid = getIntent().getExtras().getString("com.example.mentorapp.UCID");
        String confirm = getIntent().getExtras().getString("com.example.mentorapp.CONFIRM");
        //Subscribe to topic here
        defineUserType(confirm, ucid);
        loadUser(confirm);
    }

    @Override
    public void onBackPressed()
    {
        /* Can't go back here */
    }

    /* Define the user type into the shared preferences */
    private void defineUserType(String type, String ucid)
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

    /* Retrieve the user's info and their mentorship partner's info and save it into a new SharedPrefs */
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
                            String [] users = new String [2];
                            Log.d("DEBUG_OUTPUT","Server Response: "+response);
                            JSONArray array = response.getJSONArray("record");
                            JSONArray courses = response.getJSONArray("courses");
                            JSONArray m_record = response.getJSONArray("record_m");

                            /* Store Mentee data into SharedPrefs */
                            JSONObject student = array.getJSONObject(0);
                            final Mentee mentee = new Mentee(getApplicationContext(), student);
                            users[0] = mentee.getUcid();

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
                            JSONObject mentor = m_record.getJSONObject(0);
                            final Mentor mento = new Mentor(getApplicationContext(), mentor);
                            users[1] = mento.getUcid();

                            /* Subscribe to Topic */
                            FireBaseServer.getTopicID(getApplicationContext(), users, new VolleyCallback(){
                                @Override
                                public void onCallback(String callback) {
                                    if(!callback.equals("Topic ID not found!")) {
                                        mento.setTopicID(callback);
                                        FireBaseServer.subscribeToTopic(callback);
                                    }
                                    else
                                        Log.d("DEBUG_OUTPUT", callback);
                                }
                            });

                            if(new UserType(getApplicationContext()).getCurrentType().equals("mentee"))
                                new ReceivingUser(getApplicationContext(), new Mentor(getApplicationContext()).getUcid());
                            else
                                new ReceivingUser(getApplicationContext(), new Mentee(getApplicationContext()).getUcid());

                            startActivity(new Intent(getApplicationContext(), SideBar.class));
                            finish();

                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("DEBUG_OUTPUT","Volley Error: "+error);
                error.printStackTrace();
                if(error instanceof TimeoutError) {
                    Toast.makeText(
                            getApplicationContext(),
                            "Request timed out. Try Again.",
                            Toast.LENGTH_SHORT
                    ).show();
                    onBackPressed();
                }
                else if(error instanceof NetworkError) {
                    Toast.makeText(
                            getApplicationContext(),
                            "Can't connect to the internet",
                            Toast.LENGTH_SHORT
                    ).show();
                    onBackPressed();
                }
            }
        });

        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(jReq);
        jReq.setRetryPolicy(new DefaultRetryPolicy(
                5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        );
    }

    /* Signing into Firebase to connect to the user's respective notification channel */
    private void signInToFirebase(final String username, final boolean value)
    {
        final String f_email = username + "@mentoruconnect.com";
        FirebaseAuth.getInstance().signInWithEmailAndPassword(f_email, "authenticated")
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                            Log.d("DEBUG_OUTPUT", "Firebase Sign in success!");
                        else if(value)
                            FirebaseAuth.getInstance().createUserWithEmailAndPassword(f_email, "authenticated");
                        else
                            Log.d("DEBUG_OUTPUT", "Unable to connect to Firebase");
                    }
                });
    }
}