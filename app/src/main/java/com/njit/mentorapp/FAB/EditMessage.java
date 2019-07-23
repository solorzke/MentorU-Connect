package com.njit.mentorapp.FAB;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.njit.mentorapp.Model.Service.NotificationText;
import com.njit.mentorapp.Model.Service.PushMessageToFCM;
import com.njit.mentorapp.R;
import com.njit.mentorapp.Model.Service.WebServer;
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
    SharedPreferences mentee, mentor, USER_TYPE;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_message);
        mentee = getSharedPreferences("STUDENT", Context.MODE_PRIVATE);
        mentor = getSharedPreferences("MENTOR", Context.MODE_PRIVATE);
        USER_TYPE = getSharedPreferences("USER_TYPE", Context.MODE_PRIVATE);
        char_count = findViewById(R.id.char_count);
        recipient = findViewById(R.id.name);
        cancel = findViewById(R.id.cancel);
        add = findViewById(R.id.add);
        avi = findViewById(R.id.avi);
        msg = findViewById(R.id.message);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayOptions(android.support.v7.app.ActionBar.DISPLAY_SHOW_CUSTOM);
        toolbar.setTitle("Send Message");
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(!isStudent(USER_TYPE)){
            fname = mentee.getString("fname", null);
            lname = mentee.getString("lname", null);
            String ucid = mentor.getString("ucid", null);
            notifyMessageText = NotificationText.message(ucid);
        }
        else {
            fname = mentor.getString("fname", null);
            lname = mentor.getString("lname", null);
            String ucid = mentee.getString("ucid", null);
            notifyMessageText = NotificationText.message(ucid);
        }

        msg.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                char_count.setText("Char Count: " + s.toString().trim().length());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

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
                    updateMessage("updateFeedback", mentor, mentee, message);
                else
                    updateMessage("updateFeedback", mentee, mentor, message);

                PushMessageToFCM.send(getApplicationContext(), notifyMessageText[0], notifyMessageText[1]);
                postToast();
                onBackPressed();
                finish();
            }
        });
    }

    private void updateMessage(final String action, SharedPreferences sen, SharedPreferences rec, final String msg) {
        final String sender = sen.getString("ucid", null);
        final String receiver = rec.getString("ucid", null);

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
