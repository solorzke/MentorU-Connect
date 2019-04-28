package com.example.mentorapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.mentorapp.model.JSON;
import com.example.mentorapp.model.Validate;

import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity {

    AlertDialog alert, SERVER_ERROR;
    String ucid;
    String pw;
    String url = "https://web.njit.edu/~kas58/mentorDemo/login.php";
    boolean asAMentor = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Button signin = (Button) findViewById(R.id.signInBtn);
        Button register = (Button) findViewById(R.id.registerBtn);
        TextView forgotPassword = (TextView) findViewById(R.id.forgotPwdText);
        TextView help = (TextView) findViewById(R.id.helpText);
        Switch toggle = (Switch) findViewById(R.id.simpleSwitch); // initiate Switch
        //~~~~~~~~~~CREATE AN ALERT MESSAGE~~~~~~~~~~~~~~~~~~~~~~~~~
        alert = new AlertDialog.Builder(Login.this).create();
        alert.setTitle("Alert");
        alert.setMessage("You've entered the incorrect UCID/Password. Try again.");
        alert.setButton(AlertDialog.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        //~~~~~~~~~~CREATE A SERVER_ERROR MESSAGE~~~~~~~~~~~~~~~~~~~~~~~~~
        SERVER_ERROR = new AlertDialog.Builder(Login.this).create();
        SERVER_ERROR.setTitle("Error 500: Internal Server Error");
        SERVER_ERROR.setMessage("Couldn't connect with the NJIT server. Try Again.");
        SERVER_ERROR.setButton(AlertDialog.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });


        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
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

        //~~~~~~~~~~START: 'SIGN IN' IS PRESSED, BEGIN VALIDATE/VERIFY PROCESS~~~~~~~~~~~~~~~~~~~~~~~~~
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final EditText ucid_et = (EditText) findViewById(R.id.ucidText);
                final EditText pw_et = (EditText) findViewById(R.id.pwText);


                ucid = ucid_et.getText().toString();
                pw = pw_et.getText().toString();

                Validate validation = new Validate(ucid, pw);

                //~~~~~~~~~~VALIDATE/VERIFY THIS LOGIN DATA~~~~~~~~~~~~~~~~~~~~~~~~~
                if (validation.validation()) {
                    if (asAMentor == true) {
                        asMentor(ucid, pw, alert, ucid_et, pw_et, SERVER_ERROR);
                    } else {
                        RequestQueue queue = Volley.newRequestQueue(Login.this);
                        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        if (response.contains("GOODSTUDENT")) {
                                            Intent goToHome = new Intent(getApplicationContext(), JSON.class);

                                            goToHome.putExtra("com.example.mentorapp.UCID", ucid);
                                            goToHome.putExtra("com.example.mentorapp.CONFIRM", "student");
                                            startActivity(goToHome);
                                            ucid_et.getText().clear();
                                            pw_et.getText().clear();
                                            finish();

                                        } else if (response.contains("BADSTUDENT")) {
                                            System.out.println("*****************CONNECTION TO PHP SCRIPT WAS " +
                                                    "SUCCESSFUL BUT RETURNED FALSE*************************");
                                            System.out.println("*****************RESPONSE: " + response +
                                                    "UCID: " + ucid + " PW: " + pw + " *************************");

                                            alert.show();
                                        }
                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                SERVER_ERROR.show();
                            }
                        }) {
                            @Override
                            protected Map<String, String> getParams() {
                                Map<String, String> params = new HashMap<String, String>();
                                params.put("tableName", "Students");
                                params.put("username", ucid);
                                params.put("password", pw);

                                return params;
                            }
                        };

                        queue.add(stringRequest);
                    }

                } else {
                    System.out.println("************************VALIDATION " +
                            "DID NOT GO THROUGH************************");
                    alert.show();
                }
            }
        });
        //~~~~~~~~~~FINISH: SIGN IN BUTTON PROCESS~~~~~~~~~~~~~~~~~~~~~~~~~

        //~~~~~~~~~~START: REGISTRATION BUTTON PROCESS~~~~~~~~~~~~~~~~~~~~~~~~~

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToRegister = new Intent(getApplicationContext(), Registration.class);
                startActivity(goToRegister);
            }
        });

        //~~~~~~~~~~FINISH: REGISTRATION BUTTON PROCESS~~~~~~~~~~~~~~~~~~~~~~~~~

        //~~~~~~~~~~START: FORGOT_PASSWORD BUTTON PROCESS~~~~~~~~~~~~~~~~~~~~~~~~~

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri forgotPw = Uri.parse(url);
                Intent goToForgotPw = new Intent(Intent.ACTION_VIEW, forgotPw);
                if (goToForgotPw.resolveActivity(getPackageManager()) != null) {
                    startActivity(goToForgotPw);
                } else {
                    alert = new AlertDialog.Builder(Login.this).create();
                    alert.setTitle("Alert");
                    alert.setMessage("No browser detected. Please install an internet browser");
                    alert.setButton(AlertDialog.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                }
            }
        });

        //~~~~~~~~~~FINISH: FORGOT_PASSWORD BUTTON PROCESS~~~~~~~~~~~~~~~~~~~~~~~~~

        //~~~~~~~~~~START: HELP_CENTER BUTTON PROCESS~~~~~~~~~~~~~~~~~~~~~~~~~

        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToHelpCenter = new Intent(getApplicationContext(), HelpCenter.class);
                startActivity(goToHelpCenter);
            }
        });

        //~~~~~~~~~~FINISH: HELP_CENTER BUTTON PROCESS~~~~~~~~~~~~~~~~~~~~~~~~~
    }

    @Override
    public void onBackPressed() {
        //YOU CANNOT GO BACK TO PREVIOUS PAGES ONCE YOU 'LOG_OUT', THUS DISABLING 'BACK-BUTTON' HERE
    }

    private void asMentor(final String ucid, final String password, final AlertDialog err,
                          final EditText et_ucid, final EditText et_pw, final AlertDialog err2) {

        RequestQueue queue = Volley.newRequestQueue(Login.this);
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.contains("GOODMENTOR")) {
                    Intent goToHome = new Intent(getApplicationContext(), JSON.class);
                    goToHome.putExtra("com.example.mentorapp.UCID", ucid);
                    goToHome.putExtra("com.example.mentorapp.CONFIRM", "mentor");
                    startActivity(goToHome);
                    et_ucid.getText().clear();
                    et_pw.getText().clear();
                    finish();
                } else if (response.contains("BADMENTOR")) {
                    err.show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error);
                err2.show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("username", ucid);
                params.put("password", password);
                params.put("tableName", "Mentors");
                return params;
            }
        };
        queue.add(request);
    }

}

