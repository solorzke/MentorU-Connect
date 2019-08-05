package com.njit.mentorapp;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.njit.mentorapp.model.service.FireBaseCallback;
import com.njit.mentorapp.model.service.FireBaseServer;
import com.njit.mentorapp.model.service.MySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class FirebaseTest extends AppCompatActivity {

    Button b;
    EditText txt, txt2;
    String token;
    private DatabaseReference db;
    private JSONObject notification, notifcationBody;
    final private String FCM_API = "https://fcm.googleapis.com/fcm/send";
    String pairedUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase_test);
        b = findViewById(R.id.btn1);
        db = FirebaseDatabase.getInstance().getReference().child("Communication");
        txt = findViewById(R.id.editText);
        txt2 = findViewById(R.id.editText2);
        FireBaseServer.findReceivingUser("ppp43", "mentor", new FireBaseCallback() {
            @Override
            public void onCallback(String value) {
                Toast.makeText(getApplicationContext(), value, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCallback(StorageReference storageReference) {

            }

        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void setJsonObjects(String NOTIFICATION_TITLE, String NOTIFICATION_MESSAGE)
    {
        String TOPIC = "/topics/kas58ppp43"; //topic must match with what the receiver subscribed to
        notification = new JSONObject();
        notifcationBody = new JSONObject();
        try {
            notifcationBody.put("title", NOTIFICATION_TITLE);
            notifcationBody.put("message", NOTIFICATION_MESSAGE);

            notification.put("to", TOPIC);
            notification.put("data", notifcationBody);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void sendNotification(JSONObject notification) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(FCM_API, notification,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.print(response);
                        txt.getText().clear();
                        txt2.getText().clear();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(FirebaseTest.this, "Request error", Toast.LENGTH_LONG).show();
                        System.out.print("onErrorResponse: Didn't work");
                    }
                }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", "key=" + FireBaseServer.getSERVER_KEY());
                params.put("Content-Type", "application/json");
                return params;
            }
        };
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest);
    }
}

/*

 1. Create child in root object
//db.push().setValue(txt.getText().toString());
                db.addValueEventListener(new ValueEventListener() {
@Override
public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        for(DataSnapshot data : dataSnapshot.getChildren())
        {
        if(data.getValue().toString().equals("kas58"))
        {
        txt.setText(data.getValue().toString());
        }
        }
        }

@Override
public void onCancelled(@NonNull DatabaseError databaseError) {
        databaseError.getMessage();
        }
        });

        2. Assign some value to that child
 */
