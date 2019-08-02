/*
 * This class covers the sidebar drawer of the application. It's layout uses both Sidebar.xml and
 * Sidebar_header.xml to create the navigation view. The menu items below take the user to various
 * pages and serves as the main point of navigation across the application.
 */

package com.njit.mentorapp.sidebar;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
import com.google.firebase.auth.FirebaseAuth;
import com.njit.mentorapp.Login;
import com.njit.mentorapp.R;
import com.njit.mentorapp.model.tools.VolleyCallback;
import com.njit.mentorapp.toolbar.SendEmail;
import com.njit.mentorapp.toolbar.WV;
import com.njit.mentorapp.coaching_log.LogFragment;
import com.njit.mentorapp.home.HomeFrag;
import com.njit.mentorapp.model.service.FireBaseCallback;
import com.njit.mentorapp.model.service.FireBaseServer;
import com.njit.mentorapp.model.users.Mentee;
import com.njit.mentorapp.model.users.Mentor;
import com.njit.mentorapp.model.users.User;
import com.njit.mentorapp.report.ReportActivity;
import com.njit.mentorapp.settings.SettingsFragment;
import com.njit.mentorapp.model.service.WebServer;
import java.util.HashMap;
import java.util.Map;

public class SideBar extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
{
    private DrawerLayout drawer;
    public TextView user_name, user_email;
    SharedPreferences USER_TYPE;
    AlertDialog RETURN_TO_LOGIN;
    public Toolbar toolbar;
    public static NavigationView navigationView;
    public static int position;
    private User user;
    private String oppo_user;
    private Mentor mentor;
    private Mentee mentee;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sidebar);
        navigationView = findViewById(R.id.nav_view);
        USER_TYPE = getSharedPreferences("USER_TYPE", Context.MODE_PRIVATE);

        /* Define who the user is, pass their shared prefs to the User class */
        mentor = new Mentor(getApplicationContext());
        mentee = new Mentee(getApplicationContext());
        defineUserType(USER_TYPE);

        /* Calendar Drop-Down Menu */
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayOptions(android.support.v7.app.ActionBar.DISPLAY_SHOW_CUSTOM);
        toolbar.setTitle("Home");

        /* Set full name and email address to the side-bar header */
        View headerView = navigationView.getHeaderView(0);
        user_name = headerView.findViewById(R.id.header_name);
        String header_name = user.getFname() + " " + user.getLname();
        user_name.setText(header_name);
        user_email = headerView.findViewById(R.id.header_email);
        user_email.setText(user.getEmail());

        /* Set Hamburger Icon to toggle and open/close side-bar drawer  */
        drawer = findViewById(R.id.drawer_layout);
        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,
                drawer,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        /* Set default fragment to Home after signing in */
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new HomeFrag())
                .addToBackStack(null)
                .commit();
        navigationView.setCheckedItem(R.id.home_item);

        /* Switch to another specific fragment if the intent comes from an activity. */
        position = getIntent().getIntExtra("pos", 0);
        switch (position)
        {
            case 1:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new FAQFragment()).addToBackStack(null).commit();
                navigationView.setCheckedItem(R.id.faq);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        String header_name = user.getFname() + " " + user.getLname();
        user_name.setText(header_name);
        user_email.setText(user.getEmail());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mini_menu, menu);
        return true;
    }

    /* Options Menu listener with options to send email message, view the RoadMap, and report activities */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.sendEmail:
                Intent go1 = new Intent(getApplicationContext(), SendEmail.class);
                startActivity(go1);
                break;
            case R.id.roadmap:
                Intent go2 = new Intent(getApplicationContext(), WV.class);
                startActivity(go2);
                break;
            case R.id.report:
                startActivity(new Intent(getApplicationContext(), ReportActivity.class)
                        .putExtra("com.example.mentorapp.Report.activity", "")
                        .putExtra("com.example.mentorapp.Report.name", oppo_user));
                break;
            default:
                break;
        }
        return true;
    }

    /* Open the fragment whenever the menu item is selected from the side-bar */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.home_item:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new HomeFrag()).commit();
                break;
            case R.id.coachinglog:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new LogFragment()).commit();
                break;
            case R.id.settings:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new SettingsFragment()).commit();
                break;
            case R.id.log_out:
                Intent intent = new Intent(this, Login.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                signOutRequest();
                FireBaseServer.getTopicID(
                        getApplicationContext(),
                        new String [] {mentee.getUcid(), mentor.getUcid()},
                        new VolleyCallback() {
                            @Override
                            public void onCallback(String callback) {
                                if(!callback.equals("Topic ID not found!"))
                                    FireBaseServer.unsubcribeToTopic(callback);
                                else
                                    Log.d("DEBUG_OUTPUT", callback);
                            }
                });
                getSupportFragmentManager().popBackStack();
                mentor.clearSharedPrefs();
                mentee.clearSharedPrefs();
                startActivity(intent);
                finish();
                break;
            case R.id.well_being:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new WBFragment()).addToBackStack(null).commit();
                break;
            case R.id.academics:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new AcademicsFragment()).addToBackStack(null).commit();

                break;
            case R.id.social_capital:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new SocialCapitalFragment()).addToBackStack(null).commit();
                break;
            case R.id.social_media:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new SMFragment()).addToBackStack(null).commit();
                break;
            case R.id.faq:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new FAQFragment()).addToBackStack(null).commit();

                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /* Close drawer if the back button is pressed or sign out if not. */
    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else if (user.getEntry().equals("true")) {
            RETURN_TO_LOGIN = new AlertDialog.Builder(this).create();
            RETURN_TO_LOGIN.setTitle("Alert");
            RETURN_TO_LOGIN.setMessage("You're about to sign out back to the sign in screen. Do you " +
                    "want to proceed?");
            RETURN_TO_LOGIN.setButton(AlertDialog.BUTTON_NEUTRAL, "Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    signOutRequest();
                    Intent intent = new Intent(SideBar.this, Login.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    getSupportFragmentManager().popBackStack();
                    startActivity(intent);
                    finish();
                }
            });
            RETURN_TO_LOGIN.setButton(AlertDialog.BUTTON_NEGATIVE, "No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            RETURN_TO_LOGIN.show();
        }
        else {
            if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                getSupportFragmentManager().popBackStack();
            } else {
                super.onBackPressed();
            }
        }
    }

    private void defineUserType(SharedPreferences type)
    {
        if(type.getString("type", null).equals("student"))
        {
            user = new User(getApplicationContext(), "Mentee");
            oppo_user = mentor.getFullName();
        }
        else if(type.getString("type", null).equals("mentor"))
        {
            user = new User(getApplicationContext(), "Mentor");
            oppo_user = mentee.getFullName();
        }
    }

    /* Disconnect the DB connection from the query scripts on the server */
    private void signOutRequest()
    {
        final String user = this.user.getUcid();

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        StringRequest request = new StringRequest(Request.Method.POST, WebServer.getQueryLink(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("DEBUG_OUTPUT","Server Response: "+response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("DEBUG_OUTPUT","Volley Error: "+error);
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
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("user", user);
                params.put("action", "sign_out");
                return params;
            }
        };
        queue.add(request);
        request.setRetryPolicy(new DefaultRetryPolicy(
                3000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        );
    }
}