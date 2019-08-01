package com.njit.mentorapp.fab;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.njit.mentorapp.model.service.NotificationText;
import com.njit.mentorapp.model.service.PushMessageToFCM;
import com.njit.mentorapp.R;
import com.njit.mentorapp.model.service.WebServer;
import com.njit.mentorapp.model.users.Mentee;
import com.njit.mentorapp.model.users.Mentor;
import com.squareup.picasso.Picasso;
import java.util.HashMap;
import java.util.Map;

public class EditMessage extends AppCompatActivity
{
    TextView char_count, recipient, cancel, add;
    ImageView avi;
    EditText msg;
    String fname, lname;
    String [] notifyMessageText;
    SharedPreferences USER_TYPE;
    private Mentor mentor;
    private Mentee mentee;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_message);
        USER_TYPE = getSharedPreferences("USER_TYPE", Context.MODE_PRIVATE);
        mentor = new Mentor(getApplicationContext());
        mentee = new Mentee(getApplicationContext());
        char_count = findViewById(R.id.char_count);
        recipient = findViewById(R.id.name);
        cancel = findViewById(R.id.cancel);
        add = findViewById(R.id.add);
        avi = findViewById(R.id.avi);
        msg = findViewById(R.id.message);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(!isStudent(USER_TYPE)){
            fname = mentee.getFname();
            lname = mentee.getLname();
            notifyMessageText = NotificationText.message(mentor.getUcid());
        }
        else {
            fname = mentor.getFname();
            lname = mentor.getLname();
            notifyMessageText = NotificationText.message(mentee.getUcid());
        }

        msg.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String counter = "Char Count: " + s.toString().trim().length();
                char_count.setText(counter);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if(mentee.notRegistered())
            recipient.setText(mentee.getUcid());
        else
            recipient.setText(fname + " " + lname);

        Picasso.get().load("https://tinyurl.com/yyt8bga6").into(avi);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = msg.getText().toString();
                if(!isStudent(USER_TYPE))
                    updateMessage(
                            "updateFeedback",
                            mentor.getUcid(),
                            mentee.getUcid(),
                            message
                    );
                else
                    updateMessage(
                            "updateFeedback",
                            mentee.getUcid(),
                            mentor.getUcid(),
                            message
                    );
                PushMessageToFCM.send(getApplicationContext(), notifyMessageText[0], notifyMessageText[1]);
                postToast();
                onBackPressed();
                finish();
            }
        });
    }

    private void updateMessage(final String action, final String sender, final String receiver, final String msg)
    {
        RequestQueue queue = Volley.newRequestQueue(EditMessage.this);
        StringRequest request = new StringRequest(Request.Method.POST, WebServer.getQueryLink(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("DEBUG_OUTPUT","Server Response: "+response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("DEBUG_OUTPUT","Volley Error: "+error);
                error.printStackTrace();
                if(error instanceof TimeoutError)
                    Toast.makeText(
                            getApplicationContext(),
                            "Request timed out. Check your network settings.",
                            Toast.LENGTH_SHORT
                    ).show();
                else if(error instanceof NetworkError)
                    Toast.makeText(
                            getApplicationContext(),
                            "Can't connect to the internet",
                            Toast.LENGTH_SHORT
                    ).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("action", action);
                params.put("sender", sender);
                params.put("receiver", receiver);
                params.put("feedback", msg);
                return params;
            }
        };
        queue.add(request);
        request.setRetryPolicy(new DefaultRetryPolicy(
                3000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        );
    }

    private void postToast() {
        Context context = getApplicationContext();
        CharSequence text = "Message Added";
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    private boolean isStudent(SharedPreferences type) {
        if (type.getString("type", null).equals("student")) {
            return true;
        } else {
            return false;
        }
    }
}
