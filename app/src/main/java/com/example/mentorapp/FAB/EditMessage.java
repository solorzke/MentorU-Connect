package com.example.mentorapp.FAB;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.example.mentorapp.R;
import com.example.mentorapp.SideBar;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class EditMessage extends AppCompatActivity {

    TextView char_count, mentee_name, cancel, add;
    ImageView avi, logo;
    EditText msg;
    String fname, lname, url = "https://web.njit.edu/~kas58/mentorDemo/query.php";
    SharedPreferences mentee, mentor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_message);
        mentee = getSharedPreferences("STUDENT", Context.MODE_PRIVATE);
        mentor = getSharedPreferences("MENTOR", Context.MODE_PRIVATE);
        char_count = findViewById(R.id.char_count);
        mentee_name = findViewById(R.id.name);
        cancel = findViewById(R.id.cancel);
        add = findViewById(R.id.add);
        avi = findViewById(R.id.avi);
        logo = findViewById(R.id.ab_img);
        msg = findViewById(R.id.message);
        fname = mentee.getString("fname", null);
        lname = mentee.getString("lname", null);
    }

    @Override
    protected void onStart() {
        super.onStart();
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
        mentee_name.setText(fname + " " + lname);
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
                updateMessage("updateFeedback", mentor, mentee, message);
                postToast();
                onBackPressed();
                finish();
            }
        });

        logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), SideBar.class));
            }
        });
    }

    private void updateMessage(final String action, SharedPreferences mtr, SharedPreferences mte, final String msg) {
        final String mentor = mtr.getString("ucid", null);
        final String student = mte.getString("ucid", null);

        RequestQueue queue = Volley.newRequestQueue(EditMessage.this);
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("action", action);
                params.put("mentor", mentor);
                params.put("student", student);
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
}
