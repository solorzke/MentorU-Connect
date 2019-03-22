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
import com.example.mentorapp.SideBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class JSON extends AppCompatActivity {

    /*
    THIS CLASS IS TO SEND/RECEIVE A JSON OBJECT REGARDING THE USER'S INFORMATION TO SAVE AS A
    SESSION IN THE APP. THEREFORE, THERE IS NO XML/UI TIED TO THIS CLASS.
     */


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        final String ucid = getIntent().getExtras().getString("com.example.mentorapp.UCID");
        String confirm = getIntent().getExtras().getString(
                "com.example.mentorapp.CONFIRM");

        /*1. START: SENDING A REQUEST TO RECEIVE A JSON ARRAY OF THE USER'S RECORD *ONLY*
         * AFTER LOGGING IN */

        if(confirm.equals("true")){
            String url = "https://web.njit.edu/~kas58/mentorDemo/query.php";
            RequestQueue rq = Volley.newRequestQueue(JSON.this);

            Map<String, String> params = new HashMap<String, String>();
            params.put("signedIn", "true");
            params.put("UCID", ucid);
            JSONObject parameters = new JSONObject(params);

            JsonObjectRequest jReq = new JsonObjectRequest(Request.Method.POST, url, parameters, new
                    Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try{
                                // Store user data into SESSION
                                JSONArray array = response.getJSONArray("record");
                                JSONObject student = array.getJSONObject(0);
                                SharedPreferences.Editor editor = getSharedPreferences("USER",
                                        Context.MODE_PRIVATE).edit();
                                editor.putString("ucid", student.getString("ucid"));
                                editor.putString("fname", student.getString("fname"));
                                editor.putString("lname", student.getString("lname"));
                                editor.putString("email", student.getString("email"));
                                editor.apply();

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

        startActivity(new Intent(getApplicationContext(), SideBar.class));

    }

    @Override
    public void onBackPressed() {
        /* CANNOT GO BACK TO LOGIN HERE*/
    }
}
