package com.njit.mentorapp.report;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.njit.mentorapp.R;
import com.njit.mentorapp.SideBar;

public class ReportActivity extends AppCompatActivity implements View.OnClickListener {

    TextView NEXT, CANCEL, REPORTED_USER, HELP_CENTER;
    EditText other_input, ACTIVITY;
    ImageView th_1, th_2, th_3, th_other;
    LinearLayout reason_1, reason_2, reason_3, other;
    boolean r1, r2, r3, oth;
    String intentExtras, name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        NEXT = findViewById(R.id.next);
        CANCEL = findViewById(R.id.cancel);
        REPORTED_USER = findViewById(R.id.reported_user);
        ACTIVITY = findViewById(R.id.activity);
        HELP_CENTER = findViewById(R.id.help_center);
        reason_1 = findViewById(R.id.reason_1);
        reason_2 = findViewById(R.id.reason_2);
        reason_3 = findViewById(R.id.reason_3);
        other = findViewById(R.id.other);
        th_1 = findViewById(R.id.th_1);
        th_2 = findViewById(R.id.th_2);
        th_3 = findViewById(R.id.th_3);
        th_other = findViewById(R.id.th_other);
        other_input = findViewById(R.id.other_input);
        r1 = false;
        r2 = false;
        r3 = false;
        intentExtras = getIntent().getExtras().getString("com.example.mentorapp.Report.activity");
        name = getIntent().getExtras().getString("com.example.mentorapp.Report.name");
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Set Reported User & activity
        other_input.setVisibility(View.GONE);
        REPORTED_USER.setText(name);
        ACTIVITY.setText(intentExtras);
        enableEdit(isBlank(true, ACTIVITY));
        NEXT.setOnClickListener(this);
        CANCEL.setOnClickListener(this);
        reason_1.setOnClickListener(this);
        reason_2.setOnClickListener(this);
        reason_3.setOnClickListener(this);
        HELP_CENTER.setOnClickListener(this);
        other.setOnClickListener(this);
        Log.d("DEBUG_OUTPUT", String.valueOf(anyReasonChecked(r1, r2, r3, oth)));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.next:
                if(anyReasonChecked(r1, r2, r3, oth))
                {
                    boolean [] list = {r1, r2, r3, oth};
                    Intent goToNext = new Intent(getApplicationContext(), VerifyReportActivity.class);
                    goToNext.putExtra("name", REPORTED_USER.getText().toString());
                    goToNext.putExtra("msg", ACTIVITY.getText().toString());
                    goToNext.putExtra("reasons", list);
                    goToNext.putExtra("other_detail", other_input.getText().toString());
                    startActivity(goToNext);
                    finish();
                }
                else
                {
                    Toast.makeText(
                            getApplicationContext(),
                            "Fill in the fields",
                            Toast.LENGTH_SHORT
                    ).show();
                }
                break;

            case R.id.cancel:
                onBackPressed();
                break;

            case R.id.reason_1:
                /* If radio button is pressed already */
                if (r1) {
                    this.r1 = false;
                    this.th_1.setImageResource(android.R.drawable.radiobutton_off_background);
                }
                /* If radio button isn't pressed already */
                else {
                    this.r1 = true;
                    this.th_1.setImageResource(android.R.drawable.radiobutton_on_background);
                }
                break;

            case R.id.reason_2:
                /* If radio button is pressed already */
                if (r2) {
                    this.r2 = false;
                    this.th_2.setImageResource(android.R.drawable.radiobutton_off_background);
                }
                /* If radio button isn't pressed already */
                else {
                    this.r2 = true;
                    this.th_2.setImageResource(android.R.drawable.radiobutton_on_background);
                }
                break;

            case R.id.reason_3:
                /* If radio button is pressed already */
                if (r3) {
                    this.r3 = false;
                    this.th_3.setImageResource(android.R.drawable.radiobutton_off_background);
                }
                /* If radio button isn't pressed already */
                else {
                    this.r3 = true;
                    this.th_3.setImageResource(android.R.drawable.radiobutton_on_background);
                }
                break;

            case R.id.other:
                /* If radio button is pressed already */
                if (oth) {
                    this.oth = false;
                    this.th_other.setImageResource(android.R.drawable.radiobutton_off_background);
                    other_input.setVisibility(View.GONE);
                }
                /* If radio button isn't pressed already */
                else {
                    this.oth = true;
                    this.th_other.setImageResource(android.R.drawable.radiobutton_on_background);
                    other_input.setVisibility(View.VISIBLE);
                }
                break;

            case R.id.help_center:
                /* Go to the FAQ Fragment */
                Intent intent = new Intent(getApplicationContext(), SideBar.class);
                intent.putExtra("pos", 1);
                startActivity(intent);
                break;

            default:
                break;
        }
    }

    private boolean isBlank(boolean value, EditText input)
    {
        if(value)
        {
            if(input.length() > 0)
                return false;
            else
                return true;
        }
        return false;
    }

    private void enableEdit(boolean value)
    {
        if(value)
            ACTIVITY.setEnabled(true);
        else
            ACTIVITY.setEnabled(false);
    }

    private boolean anyReasonChecked(boolean r1, boolean r2, boolean r3, boolean oth)
    {
        if(ACTIVITY.length() == 0)
            return false;

        if(r1 || r2 || r3 || oth)
        {
            if((r1 || r2 || r3) && !oth)
                return true;
            else if(isBlank(oth, other_input))
                return false;
            else
                return true;
        }
        else
            return false;
    }
}
