package com.example.mentorapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import android.content.Intent;

import java.util.HashMap;
import java.util.Map;

public class Registration extends AppCompatActivity {
    String nam;
    String email;
    String id;
    String password;

    AlertDialog al;
    AlertDialog logal;
    AlertDialog SERVER_ERROR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        Button reg = (Button) findViewById(R.id.button) ;

        //Registration Error Alert
        al = new AlertDialog.Builder(Registration.this).create();
        al.setTitle("Alert");
        al.setMessage("UCID Not Found, Please see HelpDesk");
        al.setButton(AlertDialog.BUTTON_NEUTRAL, "Okay", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        //Second Alert Message
        logal = new AlertDialog.Builder(Registration.this).create();
        logal.setTitle("Alert");
        logal.setMessage("Account Already Exists. Return To Login Page");
        logal.setButton(AlertDialog.BUTTON_NEUTRAL, "Okay", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        //~~~~~~~~~~CREATE A SERVER_ERROR MESSAGE~~~~~~~~~~~~~~~~~~~~~~~~~
        SERVER_ERROR = new AlertDialog.Builder(Registration.this).create();
        SERVER_ERROR.setTitle("Error 500: Internal Server Error");
        SERVER_ERROR.setMessage("Couldn't connect with the NJIT server. Try Again.");
        SERVER_ERROR.setButton(AlertDialog.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText name = (EditText) findViewById(R.id.Name);
                EditText em = (EditText) findViewById(R.id.Email);
                EditText ucid = (EditText) findViewById(R.id.UCID);
                EditText pass = (EditText) findViewById(R.id.Pass);
                nam = name.getText().toString();
                email = em.getText().toString();
                id = ucid.getText().toString();
                password = pass.getText().toString();


                //--------------------

                System.out.println("******************************* HERE B4 REQUEST **************************************");


                RequestQueue rq = Volley.newRequestQueue(Registration.this);

                String url = "https://web.njit.edu/~kas58/mentorDemo/register.php";
                StringRequest sq = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println("******************************* HERE AFTER REQUEST START **************************************");

                        if (response.contains("REGISTERED")) {
                            Intent gth = new Intent(getApplicationContext(),SideBar.class);
                            gth.putExtra("com.example.mentorapp.CONFIRM","true");
                            startActivity(gth);

                        }
                        else if (response.contains("STUDENT_EXISTS")
                        ) {
                            System.out.println("******************************* "+ response+" **************************************");
                            logal.show();
                        }

                    }
                } ,new Response.ErrorListener ()

                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        System.out.println("******************************* HERE ERROR REQUEST **************************************");
                        SERVER_ERROR.show();

                    }

                }) {
                    @Override
                    protected Map <String, String> getParams()
                    {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("tableName","Students");
                        params.put("username",id);
                        params.put("password",password);
                        params.put("fname", "Kevin");  // <------- ERASE THE VALUE AND REPLACE WITH FNAME VARIABLE
                        params.put("lname", "Solorzano"); //<------- ERASE THE VALUE AND REPLACE WITH LNAME VARIABLE
                        params.put("email", "kas58@njit.edu"); //<-------ERASE THE VALUE AND REPLACE WITH EMAIL VARIABLE
                        return params;
                    }
                };
                rq.add(sq);
            }

        });
    }
}