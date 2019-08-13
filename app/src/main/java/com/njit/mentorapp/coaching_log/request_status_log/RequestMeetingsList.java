package com.njit.mentorapp.coaching_log.request_status_log;

import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.njit.mentorapp.R;
import com.njit.mentorapp.model.service.MySingleton;
import com.njit.mentorapp.model.service.WebServer;
import com.njit.mentorapp.model.tools.Validate;
import com.njit.mentorapp.model.users.User;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class RequestMeetingsList extends AppCompatActivity
{
    ListView pendingList, receivedList;
    TextView p_none, r_none;
    ArrayList<ArrayList<String>> pendingArray = new ArrayList<>();
    ArrayList<ArrayList<String>> receivingArray = new ArrayList<>();
    User user;
    private SwipeRefreshLayout refresh;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        setTheme(R.style.AppTheme_NoActionBar);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_meetings_list);
        pendingList = findViewById(R.id.pending);
        receivedList = findViewById(R.id.received);
        p_none = findViewById(R.id.pending_empty);
        r_none = findViewById(R.id.receiving_empty);
        refresh = findViewById(R.id.refresh_layout);

        /* Set visibility for warnings */
        p_none.setVisibility(View.GONE);
        r_none.setVisibility(View.GONE);

        /* Define the SharedPrefs based on what the user type */
        user = Validate.isStudent(getSharedPreferences("USER_TYPE", Context.MODE_PRIVATE))
                ? new User(getApplicationContext(), "Mentee")
                : new User(getApplicationContext(), "Mentor");

        /* Set the toolbar */
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayOptions(android.support.v7.app.ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle("Request Meeting List");

        /* Register the context menu for editing/removal list item when performing long clicks */
        registerForContextMenu(pendingList);
        registerForContextMenu(receivedList);
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        pendingArray.clear();
        receivingArray.clear();
        getRequests();
        /* ListView (pending) listening for a list item clicked. */
        pendingList.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                if(!((TextView)(view.findViewById(R.id.text1))).getText().toString().equals("No New Meeting Requests"))
                {
                    String title = ((TextView)(view.findViewById(R.id.text1))).getText().toString();
                    String purpose = ((TextView)(view.findViewById(R.id.text2))).getText().toString();
                    Intent intent = new Intent(getApplicationContext(), RequestTabLayout.class);
                    intent.putStringArrayListExtra("meeting_details", findArrayList(title, purpose));
                    intent.putExtra("type", "sender");
                    intent.putExtra("responder", user.getUcid());
                    startActivity(intent);
                }
            }
        });

        /* ListView (received) listening for a list item clicked. */
        receivedList.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                if(!((TextView)(view.findViewById(R.id.text1))).getText().toString().equals("No New Meeting Requests"))
                {
                    String title = ((TextView) (view.findViewById(R.id.text1))).getText().toString();
                    String purpose = ((TextView) (view.findViewById(R.id.text2))).getText().toString();
                    Intent intent = new Intent(getApplicationContext(), RequestTabLayout.class);
                    intent.putExtra("meeting_details", findArrayList(title, purpose));
                    intent.putExtra("type", "receiver");
                    intent.putExtra("responder", user.getUcid());
                    startActivity(intent);
                }
            }
        });

        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pendingArray.clear();
                receivingArray.clear();
                getRequests();
                refresh.setRefreshing(false);
            }
        });
    }

    /* Send a request to the DB to get all the meeting requests rows */
    private void getRequests()
    {
        RequestQueue rq = Volley.newRequestQueue(this);
        Map<String, String> params = new HashMap<String, String>();
        params.put("action", "getRequests");
        params.put("user", user.getUcid());
        JSONObject parameters = new JSONObject(params);

        JsonObjectRequest jReq = new JsonObjectRequest(
                Request.Method.POST,
                WebServer.getQueryLink(),
                parameters,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try
                        {
                            System.out.print("Server Response: "+response);
                            JSONArray pending = response.getJSONArray("pending");
                            JSONArray receiving = response.getJSONArray("receiving");

                            for(int i = 0; i < pending.length(); i++)
                                pendingArray.add(getJSONItems(pending.getJSONObject(i)));

                            for(int i = 0; i < receiving.length(); i++)
                                receivingArray.add(getJSONItems(receiving.getJSONObject(i)));

                            setListView(loadHashMap(pendingArray), pendingList);
                            setListView(loadHashMap(receivingArray), receivedList);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                error.printStackTrace();
                Log.d("DEBUG_OUTPUT","Volley Error: " + error);
                if(error instanceof TimeoutError)
                    Toast.makeText(
                            getApplicationContext(),
                            "Request timed out.",
                            Toast.LENGTH_SHORT
                    ).show();
                else if(error instanceof NetworkError)
                    Toast.makeText(
                            getApplicationContext(),
                            "Can't connect to the internet.",
                            Toast.LENGTH_SHORT
                    ).show();
            }
        });

        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(jReq);
        jReq.setRetryPolicy(new DefaultRetryPolicy(
                3000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        );
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

            case 999999999:
                return false;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
    {
        super.onCreateContextMenu(menu, v, menuInfo);
        String t = ((TextView)(v.findViewById(R.id.text1))).getText().toString();
        if(!t.equals("No New Meeting Requests"))
            getMenuInflater().inflate(R.menu.delete_menu, menu);
    }

    /* Retrieve the information from the list item selected, present a context menu with option
     * 'remove' and perform the actions if clicked. */
    @Override
    public boolean onContextItemSelected(MenuItem item)
    {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId())
        {
            /* Allow the user to remove the course from the listview */
            case R.id.remove:
                View view = info.targetView;
                String title = ((TextView)(view.findViewById(R.id.text1))).getText().toString();
                String purpose = ((TextView)(view.findViewById(R.id.text2))).getText().toString();
                deleteRequest(findArrayList(title, purpose));
                pendingArray.clear();
                receivingArray.clear();
                getRequests();
                return false;

            case 999999999:
                return false;

            default:
                return super.onContextItemSelected(item);
        }
    }

    /* Add all remaining requests (titles & purposes) from ArrayList to the hashmap */
    private HashMap loadHashMap(ArrayList<ArrayList<String>> rows)
    {
        HashMap<String, String> map = new HashMap<>();
        for(ArrayList <String> row : rows)
        {
            Log.d("DEBUG_OUTPUTT", row.get(3) + row.get(8));
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
        for(ArrayList<String> row : pendingArray)
            if(row.get(3).equals(n1) && row.get(8).equals(n2))
                return row;

        for(ArrayList<String> row : receivingArray)
            if(row.get(3).equals(n1) && row.get(8).equals(n2))
                return row;

        return null;
    }

    /* Remove arraylist from the ArrayList collection */
    private boolean removeArrayList(ArrayList <String> list)
    {
        for(ArrayList<String> row : pendingArray)
            if(row.equals(list)) {
                pendingArray.remove(row);
                return true;
            }

        for(ArrayList<String> row : receivingArray)
            if(row.equals(list)) {
                receivingArray.remove(row);
                return true;
            }
        return false;
    }

    /* Remove a request from the Request Log via DB */
    private void deleteRequest(ArrayList<String> list)
    {
        final String id = list.get(0);
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(
                Request.Method.POST, WebServer.getQueryLink(), new Response.Listener<String>()
        {
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
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<String, String>();
                params.put("action", "deleteRequest");
                params.put("id", id);
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