package com.njit.mentorapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.njit.mentorapp.model.JSON;
import com.njit.mentorapp.model.Validate;
import java.util.HashMap;
import java.util.Map;
import me.ibrahimsn.particle.ParticleView;

public class Login extends AppCompatActivity implements View.OnClickListener{

    private String ucid;
    private String pw;
    private TextView forgotPassword, help;
    private Button signin, register;
    private String url = "https://web.njit.edu/~kas58/mentorDemo/Model/login.php";
    boolean asAMentor = false;
    private Switch toggle;
    private EditText ucid_et, pw_et;
    ParticleView particleView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        signin = findViewById(R.id.signInBtn);
        register = findViewById(R.id.registerBtn);
        forgotPassword = findViewById(R.id.forgotPwdText);
        help = findViewById(R.id.helpText);
        toggle = findViewById(R.id.simpleSwitch); // initiate Switch
        particleView = findViewById(R.id.particleView);
        ucid_et = findViewById(R.id.ucidText);
        pw_et = findViewById(R.id.pwText);
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        signin.setOnClickListener(this);
        register.setOnClickListener(this);
        forgotPassword.setOnClickListener(this);
        help.setOnClickListener(this);
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    asAMentor = true;
                } else {
                    //do stuff when Switch if OFF
                    asAMentor = false;
                }
            }
        });
    }

    @Override
    public void onBackPressed()
    {
        /* To avoid the user from heading back to a logged out session, disable
         * the back-press button.  */
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.signInBtn:
                ucid = ucid_et.getText().toString();
                pw = pw_et.getText().toString();
                Validate validation = new Validate(ucid, pw);
                if (validation.validation())
                {
                    if (asAMentor)
                        signIn("Mentors", "mentor");
                    else
                        signIn("Students", "student");
                }
                else
                {
                    System.out.println("Validation " + "did not go through");
                    alertMessage("Alert", "You've entered the incorrect UCID/Password. Try again.", "OK");
                }
                break;

            /* Head to Register page for new mentees to register */
            case R.id.registerBtn:
                Intent goToRegister = new Intent(getApplicationContext(), Registration.class);
                startActivity(goToRegister);
                break;

            case R.id.forgotPwdText:
                String link = "http://myucid.njit.edu";
                Uri forgotPw = Uri.parse(link);
                Intent goToForgotPw = new Intent(Intent.ACTION_VIEW, forgotPw);
                if (goToForgotPw.resolveActivity(getPackageManager()) != null)
                    startActivity(goToForgotPw);
                else
                    alertMessage("Alert", "No browser detected. Please install an internet browser", "OK");
                break;

            /* Head to FAQ page for questions and answers */
            case R.id.helpText:
                Intent goToHelpCenter = new Intent(getApplicationContext(), HelpCenter.class);
                startActivity(goToHelpCenter);
                break;

            default:
                break;
        }
    }

    private void signIn(final String user_type, final String extras)
    {
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println("Server response: "+response);
                        if (response.contains("GOODSTUDENT") || response.contains("GOODMENTOR"))
                        {
                            Intent goToHome = new Intent(getApplicationContext(), JSON.class);
                            goToHome.putExtra("com.example.mentorapp.UCID", ucid);
                            goToHome.putExtra("com.example.mentorapp.CONFIRM", extras);
                            startActivity(goToHome);
                            ucid_et.getText().clear();
                            pw_et.getText().clear();
                            finish();
                        }
                        else if (response.contains("BADSTUDENT") || response.contains("BADMENTOR"))
                        {
                            System.out.println("Server Response: Connection to PHP script was " +
                                    "successful but returned false");
                            alertMessage("Alert", "You've entered the incorrect UCID/Password. Try again.", "OK");
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<String, String>();
                params.put("tableName", user_type);
                params.put("username", ucid);
                params.put("password", pw);
                return params;
            }
        };
        stringRequest.setRetryPolicy(new RetryPolicy()
        {
            @Override
            public int getCurrentTimeout() {
                return 5000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 5000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError
            {
                error.printStackTrace();
                alertMessage("Request Timed Out.", "Try Again.", "OK");
            }
        });

        queue.add(stringRequest);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        particleView.resume();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        particleView.pause();
    }

    private void alertMessage(String title, String message, String button)
    {
        /* Create Alert Message */
        AlertDialog alert = new AlertDialog.Builder(Login.this).create();
        alert.setTitle(title);
        alert.setMessage(message);
        alert.setButton(AlertDialog.BUTTON_NEUTRAL, button, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alert.show();
    }
}