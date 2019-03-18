package com.example.mentorapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;
import com.example.mentorapp.model.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Home extends AppCompatActivity {

    SharedPreferences SESSION;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        TextView name = (TextView) findViewById(R.id.name);
        SESSION = getSharedPreferences("USER",MODE_PRIVATE);
        final String ucid = SESSION.getString("ucid",null);
        String confirm = getIntent().getExtras().getString("com.example.mentorapp.CONFIRM");


        //1. START: SENDING A REQUEST TO RECEIVE A JSON ARRAY OF THE USER'S RECORD *ONLY* AFTER LOGGING IN
        if(confirm.equals("true")){
            String url = "https://web.njit.edu/~kas58/mentorDemo/query.php";
            RequestQueue rq = Volley.newRequestQueue(Home.this);

            Map<String, String> params = new HashMap<String, String>();
            params.put("signedIn", "true");
            params.put("UCID", ucid);
            JSONObject parameters = new JSONObject(params);

            JsonObjectRequest jReq = new JsonObjectRequest(Request.Method.POST, url, parameters, new
                    Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try{
                        // Get the JSON array
                        JSONArray array = response.getJSONArray("record");
                        JSONObject student = array.getJSONObject(0);
                        editor = getSharedPreferences("USER", MODE_PRIVATE).edit();
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


        name.setText("Welcome Home, " + SESSION.getString("fname", null));


    }
}