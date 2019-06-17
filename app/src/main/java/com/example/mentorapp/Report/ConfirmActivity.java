package com.example.mentorapp.Report;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.mentorapp.R;
import com.example.mentorapp.SideBar;

public class ConfirmActivity extends AppCompatActivity
{
    TextView exit, confirm_no, contact_info;
    String confirmation_no;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm);
        exit = findViewById(R.id.exit);
        confirm_no = findViewById(R.id.confirmation_no);
        contact_info = findViewById(R.id.contact_office);
        //confirmation_no = getIntent().getExtras().getString("confirmNo");
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        //confirm_no.setText(confirmation_no);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), SideBar.class));
            }
        });

        contact_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(getApplicationContext(), ContactInfoActivity.class));
            }
        });
    }
}
