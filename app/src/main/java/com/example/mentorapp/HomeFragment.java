package com.example.mentorapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class HomeFragment extends Fragment {

    /*
    *
    * THIS FRAGMENT COVERS THE HOME SCREEN & IS THE DEFAULT SCREEN THAT SIDEBAR SENDS THE USER TO
    * AFTER LOGGING IN
    *
    */

    SharedPreferences SESSION;
    SharedPreferences.Editor editor;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        TextView name = (TextView) view.findViewById(R.id.name);
        SESSION = this.getActivity().getSharedPreferences("USER", Context.MODE_PRIVATE);
        final String ucid = SESSION.getString("ucid",null);
        String confirm = this.getActivity().getIntent().getExtras().getString(
                "com.example.mentorapp.CONFIRM");

        /*1. START: SENDING A REQUEST TO RECEIVE A JSON ARRAY OF THE USER'S RECORD *ONLY*
           * AFTER LOGGING IN */

        if(confirm.equals("true")){
            String url = "https://web.njit.edu/~kas58/mentorDemo/query.php";
            RequestQueue rq = Volley.newRequestQueue(this.getActivity());

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
                                editor = SESSION.edit();
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
        return view;
    }
}
