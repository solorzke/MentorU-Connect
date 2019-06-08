package com.example.mentorapp.Events;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.mentorapp.R;
import com.example.mentorapp.SMFragment;
import com.example.mentorapp.WellBeing.WB_Article1;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class MeetingDetails extends Fragment {

    SeekBar sb;
    SharedPreferences SESSION;
    String url = "https://web.njit.edu/~kas58/mentorDemo/Model/index.php";
    TextView title, location, purpose, date, time, meeting_with, slide_title;
    View view;

    /* When the fragment view is created, initialize necessary objects */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_meeting_details, container, false);
        SESSION = getActivity().getSharedPreferences("USER", Context.MODE_PRIVATE);
        sb = (SeekBar) view.findViewById(R.id.myseek);
        title = view.findViewById(R.id.meeting_title);
        location = view.findViewById(R.id.meeting_location);
        purpose = view.findViewById(R.id.meeting_purpose);
        date = view.findViewById(R.id.meeting_date);
        time = view.findViewById(R.id.meeting_time);
        meeting_with = view.findViewById(R.id.meeting_user);
        slide_title = view.findViewById(R.id.slide_title);
        return view;
    }

    /* After fragment is created, begin running the view */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        /* Call getMeetingInfo() to receive data from web server about the meeting request
         * SeekBar slider is used to confirm the meeting request, and listens for the progress slider to
         * pass 95% of the way through */
        getMeetingInfo(view, url, "getMeetingInfo", SESSION.getString("ucid", null));
        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                /* Once the slider has completed, use confirmMeeting() to send a request to change the
                 * status of the meeting request to approved
                 * Hide SeekBar and title.*/
                if (seekBar.getProgress() > 95) {
                    confirmMeeting(view, url, "confirmMeeting", SESSION.getString("ucid", null));
                    seekBar.setVisibility(View.INVISIBLE);
                    slide_title.setVisibility(View.INVISIBLE);
                    getActivity().onBackPressed();
                } else {
                    seekBar.setProgress(0);
                    seekBar.setThumb(getResources().getDrawable(R.drawable.seekbar_thumb));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                if (progress > 95) {
                    seekBar.setThumb(getResources().getDrawable(R.drawable.seekbar_thumb));
                }

            }
        });
    }

    /* Retrieves meeting details to display on the View */

    private void getMeetingInfo(View v, String url, final String action, final String currentUser) {
        RequestQueue queue = Volley.newRequestQueue(v.getContext());
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println(response);
                if (response.equals("empty")) ;
                else {
                    String[] data = response.split("\\|");
                    title.setText(data[0]);
                    location.setText(data[1]);
                    purpose.setText(data[2]);
                    date.setText(data[3]);
                    time.setText(data[4]);
                    meeting_with.setText(data[5]);

                    /* If the request status is already accepted or denied, hide SeekBar and title */
                    if (data[6].equals("1") || data[6].equals("2")) {
                        sb.setVisibility(View.INVISIBLE);
                        slide_title.setVisibility(View.INVISIBLE);
                    }

                    /* If the current user is the sender of the meeting request, hide SeekBar and title */
                    if (data[7].equals("sender")) {
                        sb.setVisibility(View.INVISIBLE);
                        slide_title.setVisibility(View.INVISIBLE);
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

    /* Sends a web server request to change the meeting request status to approved */

    private void confirmMeeting(View v, String url, final String action, final String currentUser) {
        RequestQueue queue = Volley.newRequestQueue(v.getContext());
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Context context = getContext();
                CharSequence text = "Meeting Confirmed!";
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
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