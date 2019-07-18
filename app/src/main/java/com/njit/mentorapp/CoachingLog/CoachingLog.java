package com.njit.mentorapp.CoachingLog;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.njit.mentorapp.R;
import com.njit.mentorapp.model.Service.WebServer;
import com.njit.mentorapp.model.Validate;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class CoachingLog extends AppCompatActivity
{
    private ArrayList <ArrayList<String>> meetings = new ArrayList<>();
    private ListView listview;
    private SharedPreferences user;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coaching_log);
        listview = findViewById(R.id.list);

        /* Define the SharedPrefs based on what the user type */
        if (Validate.isStudent(getSharedPreferences("USER_TYPE", Context.MODE_PRIVATE)))
            user = getSharedPreferences("STUDENT", Context.MODE_PRIVATE);
        else
            user = getSharedPreferences("MENTOR", Context.MODE_PRIVATE);

        /* Set the toolbar */
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayOptions(android.support.v7.app.ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle("Coaching Log");

        /* Register the context menu for editing/removal list item when performing long clicks */
        registerForContextMenu(listview);
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        meetings.clear();
        getMeetings();
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                String title = ((TextView)(view.findViewById(R.id.text1))).getText().toString();
                String purpose = ((TextView)(view.findViewById(R.id.text2))).getText().toString();
                Intent intent = new Intent(getApplicationContext(), Meeting.class);
                intent.putStringArrayListExtra("meeting_details", findArrayList(title, purpose));
                startActivity(intent);
            }
        });
    }

    /* Send a request to the DB to get all the meeting requests rows */
    private void getMeetings()
    {
        RequestQueue rq = Volley.newRequestQueue(this);
        Map<String, String> params = new HashMap<String, String>();
        params.put("action", "getAcceptedMeetings");
        params.put("user", user.getString("ucid", null));
        JSONObject parameters = new JSONObject(params);

        JsonObjectRequest jReq = new JsonObjectRequest(Request.Method.POST, WebServer.getQueryLink(), parameters, new
                Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try
                        {
                            System.out.print("Server Response: "+response);
                            JSONArray json_meetings = response.getJSONArray("meetings");

                            for(int i = 0; i < json_meetings.length(); i++)
                                meetings.add(getJSONItems(json_meetings.getJSONObject(i)));

                            setListView(loadHashMap(meetings), listview);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("Volley Error: " + error);
                error.printStackTrace();
            }
        });

        rq.add(jReq);
    }

    /* Remove a meeting from the Coaching Log via DB */
    private void deleteMeeting(ArrayList<String> list)
    {
        final String id = list.get(0);
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.POST, WebServer.getQueryLink(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("Server Response: "+response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("Volley Error: "+error);
                error.printStackTrace();
            }
        }) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<String, String>();
                params.put("action", "deleteMeeting");
                params.put("id", id);
                return params;
            }
        };
        queue.add(request);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
    {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.delete_menu, menu);
    }

    /* Retrieve the information from the list item selected, present a context menu with option
     * 'remove' and perform the actions if clicked. */
    @Override
    public boolean onContextItemSelected(MenuItem item)
    {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        View v = info.targetView;
        String t = ((TextView)(v.findViewById(R.id.text1))).getText().toString();
        if(t.equals("No New Meeting Requests"))
            return super.onContextItemSelected(item);

        switch (item.getItemId())
        {
            /* Allow the user to remove the course from the listview */
            case R.id.remove:
                View view = info.targetView;
                String title = ((TextView)(view.findViewById(R.id.text1))).getText().toString();
                String purpose = ((TextView)(view.findViewById(R.id.text2))).getText().toString();
                deleteMeeting(findArrayList(title, purpose));
                meetings.clear();
                getMeetings();
                return false;

            default:
                return super.onContextItemSelected(item);
        }
    }

    /* When clicking the back button, go back to the last page. */
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                onBackPressed();
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /* Add all remaining requests (titles & purposes) from ArrayList to the hashmap */
    private HashMap loadHashMap(ArrayList<ArrayList<String>> rows)
    {
        HashMap<String, String> map = new HashMap<>();
        for(ArrayList <String> row : rows)
        {
            map.put(row.get(3), row.get(8));
        }

        return map;
    }

    /* Load the hashmap into the function (containing the requests) and set the adapter to the list view */
    private void setListView(HashMap map, ListView lv)
    {
        List<HashMap<String, String>> items = new ArrayList<>();
        SimpleAdapter adapter = new SimpleAdapter(this, items, R.layout.list_item,
                new String[]{"First Line", "Second Line"}, new int[]{R.id.text1, R.id.text2});
        Iterator it = map.entrySet().iterator();

        while (it.hasNext())
        {
            HashMap<String, String> results = new HashMap<>();
            Map.Entry pair = (Map.Entry) it.next();
            results.put("First Line", pair.getKey().toString());
            results.put("Second Line", pair.getValue().toString());
            items.add(results);
        }
        lv.setAdapter(adapter);
    }

    /* Use this function to return an ArrayList loaded with all JSONArray data  */
    private ArrayList<String> getJSONItems(JSONObject row)
    {
        ArrayList<String> list = new ArrayList<>();
        try
        {
            list.add(row.getString("id"));               //<!--- [0]
            list.add(row.getString("sender"));          //<!--- [1]
            list.add(row.getString("receiver"));       //<!--- [2]
            list.add(row.getString("title"));         //<!--- [3]
            list.add(row.getString("e_date"));       //<!--- [4]
            list.add(row.getString("start_time"));  //<!--- [5]
            list.add(row.getString("end_time"));   //<!--- [6]
            list.add(row.getString("location"));  //<!--- [7]
            list.add(row.getString("purpose"));  //<!--- [8]
            list.add(row.getString("status"));  //<!--- [9]
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        return list;
    }

    /* Find the ArrayList associated with the title and purpose of request. */
    private ArrayList <String> findArrayList(String n1, String n2)
    {
        for(ArrayList<String> row : meetings)
            for(int i = 0; i < row.size(); i++)
                if(row.get(i).equals(n1) || row.get(i).equals(n2))
                    return row;
        return null;
    }
}
