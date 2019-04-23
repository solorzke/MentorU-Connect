package com.example.mentorapp;

import android.app.AlertDialog;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mentorapp.Events.AddEvent;
import com.example.mentorapp.Events.RequestMeeting;
import com.example.mentorapp.Events.RequestStatus;

public class SideBar extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    /*
    THIS CLASS COVERS THE SIDEBAR DRAWER OF THE APP. IT'S CONNECTED WITH BOTH SIDEBAR.XML
    AND SIDEBAR_HEADER.XML TO CREATE THE NAVIGATION VIEW FOR IT. HERE ARE THE MENU ITEMS THAT TAKE YOU
    TO DIFFERENT FRAGMENTS TO VISIT WHEN SELECTED.

    SIDEBAR CODE IS RELEVANT WHEN THE DRAWER IS OPENED.
     */

    private DrawerLayout drawer;
    TextView user_name, user_email;
    ImageView AC_IMG;
    SharedPreferences SESSION;
    SharedPreferences.Editor editor;
    AlertDialog RETURN_TO_LOGIN;
    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sidebar);
        navigationView = findViewById(R.id.nav_view);
        SESSION = getSharedPreferences("USER", MODE_PRIVATE);


        /* CALENDAR DROP DOWN MENU */
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayOptions(android.support.v7.app.ActionBar.DISPLAY_SHOW_CUSTOM);

        /* Set Action Bar image to redirect user back to the home page */
        AC_IMG = (ImageView) findViewById(R.id.ab_img);
        AC_IMG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new HomeFragment()).addToBackStack(null).commit();
                navigationView.setCheckedItem(R.id.home_item);
            }
        });

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
                new HomeFragment()).addToBackStack(null).commit();
        navigationView.setCheckedItem(R.id.home_item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.events, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.createevent:
                Intent go = new Intent(getApplicationContext(), AddEvent.class);
                startActivity(go);
                break;
            case R.id.requestmeeting:
                Intent go1 = new Intent(getApplicationContext(), RequestMeeting.class);
                startActivity(go1);
                break;
            case R.id.viewmeeting:
                Intent go2 = new Intent(getApplicationContext(), RequestStatus.class);
                startActivity(go2);
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
                        new HomeFragment()).commit();
                break;
            case R.id.account:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new AccountFragment()).commit();
                break;
            case R.id.log_out:
                Intent intent = new Intent(this, Login.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getSupportFragmentManager().popBackStack();
                startActivity(intent);
                finish();
                break;
            case R.id.personal_excel:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new WBFragment()).addToBackStack(null).commit();
                break;
            case R.id.academics:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new AcademicsFragment()).addToBackStack(null).commit();

                break;
            case R.id.social_capital:
                /* getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new <ADD_SOCIAL_CAPITAL_FRAGMENT_HERE>.class).addToBackStack(null).commit();
                 */
                break;
            case R.id.social_media:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new SMFragment()).addToBackStack(null).commit();
                break;
            case R.id.clubs:
                /* getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new <ADD_CLUBS_FRAGMENT_HERE>.class).addToBackStack(null).commit();
                 */
                break;
            case R.id.faq:
                /* getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new <ADD_FAQ_FRAGMENT_HERE>.class).addToBackStack(null).commit();
                 */
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

}