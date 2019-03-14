package com.example.mentorapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class Home extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        if(getIntent().hasExtra("com.example.mentorship_app.USER")){
            TextView name = (TextView) findViewById(R.id.name);
            String name_user = getIntent().getExtras().getString("com.example.mentorship_app.USER");
            name.setText(name_user);
        }

    }
}