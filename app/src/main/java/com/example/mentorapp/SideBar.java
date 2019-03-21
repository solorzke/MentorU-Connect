package com.example.mentorapp;

import android.app.ActionBar;
import android.content.Context;
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
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import org.w3c.dom.Text;

public class SideBar extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawer;
    TextView title, user_name, user_email;
    SharedPreferences SESSION;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sidebar);

        SESSION = getSharedPreferences("USER", MODE_PRIVATE);

        //POSITION THE TOOLBAR'S TITLE TO THE FAR RIGHT
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayOptions(android.support.v7.app.ActionBar.DISPLAY_SHOW_CUSTOM);
        title = (TextView) toolbar.findViewById(R.id.action_bar_title);
        title.setText("Home");

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        //UPDATE HEADER NAME AND EMAIL OF THE USER IN SESSION
        View headerView = navigationView.getHeaderView(0);
        user_name = (TextView) headerView.findViewById(R.id.header_name);
        String header_name = SESSION.getString("fname", null) + " " +
                SESSION.getString("lname", null);
        user_name.setText(header_name);
        user_email = (TextView) headerView.findViewById(R.id.header_email);
        String header_email = SESSION.getString("email", null);
        user_email.setText(header_email);

        //SET ACTIONBAR HAMBURGER TO TOGGLE THE SIDEBAR NAVIGATION WHEN PRESSED
        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        //THIS IS THE DEFAULT FRAGMENT TO OPEN INTO WHEN YOU LOGIN IN - HOME
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new HomeFragment()).commit();
        navigationView.setCheckedItem(R.id.home_item);

    }

    //OPEN A FRAGMENT IF A MENU ITEM IS SELECTED FROM THE SIDE BAR MENU
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.home_item:
                title.setText("Home");
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new HomeFragment()).commit();
                break;
            case R.id.account:
                title.setText("Account");
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new HomeFragment()).commit();
                break;
            case R.id.log_out:
                Intent i = new Intent(getApplicationContext(), Login.class);
                startActivity(i);
                break;
            case R.id.well_being:
                title.setText("Well Being");
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new HomeFragment()).commit();
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }
        else{
            super.onBackPressed();
        }
        super.onBackPressed();
    }
}