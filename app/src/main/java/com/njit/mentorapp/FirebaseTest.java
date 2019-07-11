package com.njit.mentorapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseTest extends AppCompatActivity {

    Button b;
    private DatabaseReference db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase_test);
        b = findViewById(R.id.btn1);
        db = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    protected void onStart() {
        super.onStart();
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /* 1. Create child in root object */
                db.child("Name").setValue("Alex Castillo");

                /* 2. Assign some value to that child */

            }
        });
    }
}
