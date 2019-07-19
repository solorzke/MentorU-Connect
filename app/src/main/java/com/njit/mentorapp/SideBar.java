/*
 * This class covers the sidebar drawer of the application. It's layout uses both Sidebar.xml and
 * Sidebar_header.xml to create the navigation view. The menu items below take the user to various
 * pages and serves as the main point of navigation across the application.
 */

package com.njit.mentorapp;

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
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.njit.mentorapp.CoachingLog.LogFragment;
import com.njit.mentorapp.Events.AddEvent;
import com.njit.mentorapp.Home.HomeFrag;
import com.njit.mentorapp.Report.ReportActivity;
import com.njit.mentorapp.Settings.SettingsFragment;
import com.njit.mentorapp.Model.Service.WebServer;
import java.util.HashMap;
import java.util.Map;

public class SideBar extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
{
    private DrawerLayout drawer;
    public TextView user_name, user_email;
    SharedPreferences SESSION, USER_TYPE;
    SharedPreferences.Editor editor;
    AlertDialog RETURN_TO_LOGIN;
    public Toolbar toolbar;
    public static NavigationView navigationView;
    public static int position;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sidebar);
        navigationView = findViewById(R.id.nav_view);
        USER_TYPE = getSharedPreferences("USER_TYPE", Context.MODE_PRIVATE);

        //ADD IF/ELSE STATEMENT TO CHECK IF THE PERSON SIGNED IN IS MENTOR/STUDENT TO INITIALIZE THE
        //SESSION SHARED PREFS INSTANCE
        defineUserType(USER_TYPE);

        /* CALENDAR DROP DOWN MENU */
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayOptions(android.support.v7.app.ActionBar.DISPLAY_SHOW_CUSTOM);
        toolbar.setTitle("Home");

        /* UPDATE HEADER NAME AND EMAIL OF THE USER IN SESSION */
        View headerView = navigationView.getHeaderView(0);
        user_name = (TextView) headerView.findViewById(R.id.header_name);
        String header_name = SESSION.getString("fname", null) + " " +
                SESSION.getString("lname", null);
        user_name.setText(header_name);
        user_email = (TextView) headerView.findViewById(R.id.header_email);
        String header_email = SESSION.getString("email", null);
        user_email.setText(header_email);

        /* SET ACTION_BAR HAMBURGER TO TOGGLE THE SIDEBAR NAVIGATION WHEN PRESSED */
        drawer = findViewById(R.id.drawer_layout);
        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        /* THIS IS THE DEFAULT FRAGMENT TO OPEN INTO WHEN YOU LOGIN IN - HOME */
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new HomeFrag()).addToBackStack(null).commit();
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
        String header_name = SESSION.getString("fname", null) + " " +
                SESSION.getString("lname", null);
        user_name.setText(header_name);
        String header_email = SESSION.getString("email", null);
        user_email.setText(header_email);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.mini_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.createevent:
                Intent go = new Intent(getApplicationContext(), AddEvent.class);
                startActivity(go);
                break;
            case R.id.sendEmail:
                Intent go1 = new Intent(getApplicationContext(), SendEmail.class);
                startActivity(go1);
                break;
            case R.id.roadmap:
                Intent go2 = new Intent(getApplicationContext(), WV.class);
                startActivity(go2);
                break;
            case R.id.report:
                startActivity(new Intent(getApplicationContext(), ReportActivity.class));
                break;
            default:
                break;
        }
        return true;
    }

    /* OPEN A FRAGMENT IF A MENU ITEM IS SELECTED FROM THE SIDE BAR MENU */
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
                signOutRequest(SESSION);
                getSupportFragmentManager().popBackStack();
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

    /* CLOSE SIDE_BAR DRAWER IF OPENED W/ BACK_BUTTON */
    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else if (SESSION.getString("firstEntry", null).equals("true")) {
            RETURN_TO_LOGIN = new AlertDialog.Builder(this).create();
            RETURN_TO_LOGIN.setTitle("Alert");
            RETURN_TO_LOGIN.setMessage("You're about to sign out back to the sign in screen. Do you " +
                    "want to proceed?");
            RETURN_TO_LOGIN.setButton(AlertDialog.BUTTON_NEUTRAL, "Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    signOutRequest(SESSION);
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
            SESSION = getSharedPreferences("STUDENT", MODE_PRIVATE);
        }
        else if(type.getString("type", null).equals("mentor"))
        {
            SESSION = getSharedPreferences("MENTOR", MODE_PRIVATE);
        }
    }

    private void signOutRequest(SharedPreferences USER)
    {
        final String user = USER.getString("ucid", null);

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
    }

}