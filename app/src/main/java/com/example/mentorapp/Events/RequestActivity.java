package com.example.mentorapp.Events;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.mentorapp.R;
import com.example.mentorapp.model.TabAdapter;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class RequestActivity extends AppCompatActivity {
    private TabAdapter adapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private int[] tabIcons = {
            R.drawable.ic_notifications,
            R.drawable.ic_organizational_skills,
    };
    TextView cancel;
    String url = "https://web.njit.edu/~kas58/mentorDemo/query.php";
    SharedPreferences SESSION;
    Timer timer;

    /* This activity controls the two fragments used to view meeting request statuses and details.
    Initialize necessary objects when activity is created */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.request_status);

        /* Set the toolbar within the activity, followed by the ViewPager and TabLayout to display tabs */
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayOptions(android.support.v7.app.ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        timer = new Timer();
        SESSION = getSharedPreferences("USER", Context.MODE_PRIVATE);
        cancel = (TextView) findViewById(R.id.cancel_btn);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        adapter = new TabAdapter(getSupportFragmentManager());
        adapter.addFragment(new MeetingStatus(), "Request Status");
        adapter.addFragment(new MeetingDetails(), "Meeting Details");
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
    }

    /* On start of the activity, check which tab the current user is viewing */
    @Override
    protected void onStart() {
        super.onStart();
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            /* If the current user is viewing the second fragment, run a timer and continuously send
            * requests to the web server to check if the status of the meeting request has changed.
            * */
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(tab.getPosition() == 1){
                    timer.schedule( new TimerTask() {
                        public void run() {
                            getMeetingInfo(url, "getMeetingInfo", SESSION.getString("ucid", null));
                        }
                    }, 0, 1*1000);

                    /* If the cancel btn is clicked, cancel the meeting, stop the timer, and return
                    * to the home page*/
                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            cancelMeeting(url, "cancelMeeting", SESSION.getString("ucid", null));
                            cancel.setVisibility(View.INVISIBLE);
                            timer.cancel();
                            onBackPressed();
                        }
                    });
                }

                /* If current user is viewing the first fragment, hide the cancel btn */
                else{
                    cancel.setVisibility(View.INVISIBLE);
                    timer.cancel();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) { }

            @Override
            public void onTabReselected(TabLayout.Tab tab) { }
        });
    }

    /* If back button is clicked, return to previous page */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /* If the activity is closed, stop the timer */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        timer.cancel();
    }

    /* Retrieves meeting request info */
    private void getMeetingInfo(String url, final String action, final String currentUser) {
        RequestQueue queue = Volley.newRequestQueue(RequestActivity.this);
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println(response);

                /* If the meeting request was already accepted or declined, hide cancel btn */
                if (response.equals("empty")) ;
                else {
                    String[] data = response.split("\\|");
                    if(data[6].equals("1") || data[6].equals("2")){
                        cancel.setVisibility(View.INVISIBLE);
                        timer.cancel();
                    }
                    else{
                        cancel.setVisibility(View.VISIBLE);
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("action", action);
                params.put("currentUser", currentUser);
                return params;
            }
        };
        queue.add(request);
    }

    /* Send a web request to change the status of the meeting request to declined  & return back to
    * home page */
    private void cancelMeeting(String url, final String action, final String currentUser){
        RequestQueue queue = Volley.newRequestQueue(RequestActivity.this);
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                cancel.setVisibility(View.INVISIBLE);
                Context context = getApplicationContext();
                CharSequence text = "Meeting Request Canceled!";
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
                onBackPressed();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("action", action);
                params.put("currentUser", currentUser);
                return params;
            }
        };
        queue.add(request);
    }

}