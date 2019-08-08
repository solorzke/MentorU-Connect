package com.njit.mentorapp.account;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.njit.mentorapp.R;
import com.njit.mentorapp.model.service.WebServer;
import com.njit.mentorapp.model.tools.Validate;
import com.njit.mentorapp.model.users.Mentee;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Courses extends AppCompatActivity implements View.OnClickListener
{
    private ImageView add, cancel;
    private CircularImageView avi;
    private TextView semester, mentee;
    private ListView list;
    private SharedPreferences courses;
    private String full_name;
    private EditText c_num, c_title;
    private EditText new_id,new_title;
    private boolean toggle = false;
    private String row_id;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courses);

        /* Set Toolbar and back button */
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayOptions(android.support.v7.app.ActionBar.DISPLAY_SHOW_CUSTOM);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setTitle("Courses");

        /* Set the UI components to their respective ID's */
        mentee = findViewById(R.id.mentee);
        add = findViewById(R.id.add);
        semester = findViewById(R.id.semester);
        list = findViewById(R.id.courses);
        c_num = findViewById(R.id.c_id);
        c_title = findViewById(R.id.c_title);
        cancel = findViewById(R.id.cancel);
        avi = findViewById(R.id.picasso);

        /* Set visibility for the edit text fields and other buttons */
        c_num.setVisibility(View.GONE);
        c_title.setVisibility(View.GONE);
        cancel.setVisibility(View.INVISIBLE);

        /* Set sharedPrefs and full name */
        courses = getSharedPreferences("COURSES", Context.MODE_PRIVATE);
        Mentee mte = new Mentee(getApplicationContext());
        full_name = mte.getFullName();

        /* Register the context menu for editing/removal list item when performing long clicks */
        registerForContextMenu(list);
        if(!mte.getAvi().equals(""))
            Picasso.get().load(mte.getAvi()).into(avi);

    }

    @Override
    protected void onStart()
    {
        super.onStart();
        mentee.setText(full_name);
        add.setOnClickListener(this);
        cancel.setOnClickListener(this);
        changeSemesterYear(semester);
        loadCourses(loadHashMap());
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
    {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.course_edit, menu);
    }

    /* Retrive the information from the list item selected, present a context menu with options 'edit' or
     * 'remove' and perform the relative actions for each one if clicked. */
    @Override
    public boolean onContextItemSelected(MenuItem item)
    {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        switch (item.getItemId())
        {
            /* Allow the user to edit the current course id and title */
            case R.id.edit:
                View view = info.targetView;
                String course_id = ((TextView)(view.findViewById(R.id.text1))).getText().toString();
                String course_title = ((TextView)(view.findViewById(R.id.text2))).getText().toString();
                showInputBox(course_id, course_title);
                return false;

            /* Allow the user to remove the course from the listview */
            case R.id.remove:
                View view2 = info.targetView;
                String remove_id = ((TextView)(view2.findViewById(R.id.text1))).getText().toString();
                String id = getRowId(courses, remove_id);
                removeCourse(id);
                updateCourseDB("", "", id);
                loadCourses(loadHashMap());
                return false;

            default:
                return super.onContextItemSelected(item);
        }
    }

    /* Click listener for 'add', 'cancel' */
    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.add:
                /* If add wasn't clicked, set the edit text fields to visible */
                if(!toggle)
                {
                    toggle = true;
                    c_num.setVisibility(View.VISIBLE);
                    c_title.setVisibility(View.VISIBLE);
                    cancel.setVisibility(View.VISIBLE);
                    add.setImageResource(R.drawable.ic_done);
                }
                /* If add was already clicked, set the edit text fields to invisible and collect their
                 * input (class id & title), add the course to the SharedPrefs, update the DB, and refresh
                  * the list view */
                else
                {
                    toggle = false;
                    add.setImageResource(R.drawable.ic_add);
                    cancel.setVisibility(View.INVISIBLE);
                    String title = c_title.getText().toString();
                    String id = c_num.getText().toString();
                    c_num.setVisibility(View.GONE);
                    c_title.setVisibility(View.GONE);
                    if(Validate.isBlank(title) || Validate.isBlank(id))
                    {
                        final AlertDialog alert = new AlertDialog.Builder(Courses.this).create();
                        alert.setTitle("Blank Fields");
                        alert.setMessage("Please don't leave blanks and fill in the fields before adding.");
                        alert.setButton(AlertDialog.BUTTON_POSITIVE, "Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                alert.dismiss();
                            }
                        });
                        alert.show();
                    }

                    else
                    {
                        addNewCourse(id, title);
                        loadCourses(loadHashMap());
                        c_num.getText().clear();
                        c_title.getText().clear();
                    }
                }
                break;
            /* Cancel the add-class procedure and erase any input text the user typed in */
            case R.id.cancel:
                toggle = false;
                cancel.setVisibility(View.INVISIBLE);
                c_num.setVisibility(View.GONE);
                c_title.setVisibility(View.GONE);
                c_num.getText().clear();
                c_title.getText().clear();
                add.setImageResource(R.drawable.ic_add);
                break;

            default:
                break;
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
            case 999999999:
                return false;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /* Add new course id and title to the SharedPrefs by finding a blank record inside */
    private void addNewCourse(String c_id, String c_title)
    {
        SharedPreferences.Editor e = courses.edit();
        for(int i = 0; i < 6; i++)
        {
            String id = this.courses.getString("row_id" + i, null);
            String num = this.courses.getString("id" + i, null);
            String title = this.courses.getString("title" + i, null);

            if (num.equals("") && title.equals("")) {
                this.row_id = id;
                e.putString("id" + i, c_id);
                e.putString("title" + i, c_title);
                e.apply();
                updateCourseDB(c_id, c_title, this.row_id);
                break;
            }

            else if(i == 5)
            {
                AlertDialog error = new AlertDialog.Builder(Courses.this).create();
                error.setTitle("Reached Course Limit");
                error.setMessage("Only a maximum of 6 courses can be added.");
                error.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                error.show();
                break;
            }
        }
    }

    /* Submit the new added course to the DB to update the list */
    private void updateCourseDB(final String c_id, final String c_title, final String row_id)
    {
        RequestQueue queue = Volley.newRequestQueue(this);
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
                Map<String, String> params = new HashMap<>();
                params.put("action", "updateCourses");
                params.put("c_id", c_id);
                params.put("c_title", c_title);
                params.put("id", row_id);
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

    /* Remove course from the shared prefs */
    private void removeCourse(String row_id)
    {
        SharedPreferences.Editor e = courses.edit();
        for (int i = 0; i < 6; i++)
        {
            if(this.courses.getString("row_id" + i, null).equals(row_id))
            {
                e.putString("id" + i, "");
                e.putString("title" + i, "");
                e.apply();
                break;
            }
        }
    }

    /* Change the TextView displaying the semester & year based on the current time */
    private void changeSemesterYear(TextView semester)
    {
        Calendar cal = Calendar.getInstance();
        int month = cal.get(Calendar.MONTH);
        String year = Integer.toString(cal.get(Calendar.YEAR));
        Log.d("DEBUG_OUTPUT","Current Year: "+year);
        if (month > 7) {
            String sem = "Fall " + year;
            semester.setText(sem);
        } else if (month < 5) {
            String sem = "Spring " + year;
            semester.setText(sem);
        } else if (4 < month || month < 8) {
            String sem = "Summer " + year;
            semester.setText(sem);
        }
    }

    /* Add all remaining courses (id & title) from SharedPrefs to the hashmap */
    private HashMap loadHashMap()
    {
        HashMap<String, String> map = new HashMap<>();

        for (int i = 0; i < 6; i++)
        {
            String c_num = this.courses.getString("id" + i, null);
            String c_title = this.courses.getString("title" + i, null);

            if (!c_num.equals("") && !c_title.equals(""))
                map.put(c_num, c_title);
        }
        return map;
    }

    /* Load the hashmap into the function (containing the courses) and set the adapter to the list view */
    private void loadCourses(HashMap map)
    {
        List<HashMap <String, String>> items = new ArrayList<>();
        SimpleAdapter adapter = new SimpleAdapter(this, items, R.layout.list_item,
                new String []{"First Line", "Second Line"}, new int [] {R.id.text1, R.id.text2} );
        Iterator it = map.entrySet().iterator();

        while(it.hasNext())
        {
            HashMap <String, String> results = new HashMap<>();
            Map.Entry pair = (Map.Entry) it.next();
            results.put("First Line", pair.getKey().toString());
            results.put("Second Line", pair.getValue().toString());
            items.add(results);
        }
        this.list.setAdapter(adapter);
    }

    /* Display the dialog layout for editing an existing course item in the list view.
     * Save the changes, update the SharedPrefs, update the DB, and reload the List view.  */
    private void showInputBox(final String old_id, String old_title)
    {
        final Dialog dialog = new Dialog(Courses.this);
        dialog.setTitle("Edit Course");
        dialog.setContentView(R.layout.custom_dialog);

        new_id = dialog.findViewById(R.id.c_id);
        new_title = dialog.findViewById(R.id.c_title);
        Button bt = dialog.findViewById(R.id.btdone);

        new_id.setText(old_id);
        new_title.setText(old_title);

        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(Validate.isBlank(new_id.getText().toString()) || Validate.isBlank(new_title.getText().toString()))
                {
                    dialog.dismiss();
                    final AlertDialog alert = new AlertDialog.Builder(Courses.this).create();
                    alert.setTitle("Blank Fields");
                    alert.setMessage("Please don't leave blanks and fill in the fields before saving.");
                    alert.setButton(AlertDialog.BUTTON_POSITIVE, "Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            alert.dismiss();
                        }
                    });
                    alert.show();
                }

                else
                {
                    String row_id = getRowId(courses, old_id);
                    updateCourse(new_id.getText().toString(), new_title.getText().toString(), row_id);
                    updateCourseDB(new_id.getText().toString(), new_title.getText().toString(), row_id);
                    loadCourses(loadHashMap());
                    dialog.dismiss();
                }
            }
        });
        dialog.show();
    }

    /* Getting the row_id from SharedPrefs based on the course_id it's associated with */
    private String getRowId(SharedPreferences prefs, String c_id)
    {
        for(int i = 0; i < 6; i++)
            if (c_id.equals(prefs.getString("id"+i, null)))
                return prefs.getString("row_id"+i, null);
        return null;
    }

    /* Update course item by using the row_id associated with the item to update the title and course id  */
    private void updateCourse(String c_id, String c_title, String row_id)
    {
        SharedPreferences.Editor e = courses.edit();
        for(int i = 0; i < 6; i++)
            if(this.courses.getString("row_id"+i, null).equals(row_id))
            {
                e.putString("id"+i, c_id);
                e.putString("title"+i, c_title);
                e.apply();
                break;
            }
    }
}