package com.njit.mentorapp;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
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
import com.njit.mentorapp.model.service.Connectivity;
import com.njit.mentorapp.model.tools.JSON;
import com.njit.mentorapp.model.service.WebServer;
import com.njit.mentorapp.model.tools.Validate;
import java.util.HashMap;
import java.util.Map;
import me.ibrahimsn.particle.ParticleView;

public class Login extends AppCompatActivity implements View.OnClickListener
{
    private String ucid;
    private String pw;
    private TextView forgotPassword, help;
    private Button signin, register;
    boolean asAMentor = false;
    private Switch toggle;
    private EditText ucid_et, pw_et;
    ParticleView particleView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        setTheme(R.style.AppTheme_NoActionBar);
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
                if(Connectivity.checkService(getApplicationContext())){
                    ucid = ucid_et.getText().toString();
                    pw = pw_et.getText().toString();
                    Validate validation = new Validate(ucid, pw);
                    if (validation.validation()) {
                        if (asAMentor)
                            signIn("Mentors", "mentor");
                        else
                            signIn("Students", "student");
                    } else {
                        Log.d("DEBUG_OUTPUT", "Validation did not go through");
                        Toast.makeText(
                                getApplicationContext(),
                                "You've entered the incorrect UCID/Password. Try again.",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                }
                else
                    Toast.makeText(
                            getApplicationContext(),
                            "Unable to connect to the internet",
                            Toast.LENGTH_SHORT
                    ).show();
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
                    Toast.makeText(
                            getApplicationContext(),
                            "No browser detected. Please install an internet browser",
                            Toast.LENGTH_SHORT
                    ).show();
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

    /* Verify whether the user can sign in or not */
    private void signIn(final String user_type, final String extras)
    {
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, WebServer.getLoginLink(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("DEBUG_OUTPUT","Server Response: "+response);
                        if (response.contains("GOODSTUDENT") || response.contains("GOODMENTOR"))
                        {
                            Intent goToHome = new Intent(getApplicationContext(), JSON.class);
                            goToHome.putExtra("com.example.mentorapp.UCID", ucid);
                            goToHome.putExtra("com.example.mentorapp.CONFIRM", extras);
                            startActivity(goToHome);
                            ucid_et.getText().clear();
                            pw_et.getText().clear();
                        }
                        else if (response.contains("BADSTUDENT") || response.contains("BADMENTOR"))
                        {
                            Log.d("DEBUG_OUTPUT","Server Response: Connection to PHP script was " +
                                    "successful but returned false");
                            Toast.makeText(
                                    getApplicationContext(),
                                    "You've entered the incorrect UCID/Password. Try again.",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
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
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<String, String>();
                params.put("tableName", user_type);
                params.put("username", ucid);
                params.put("password", pw);
                return params;
            }
        };

        queue.add(stringRequest);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                3000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        );
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
}