package com.example.mentorapp.CoachingLog.RequestStatusLog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.mentorapp.R;
import com.example.mentorapp.model.DateTimeFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MeetingDetails extends Fragment
{
    String url = "https://web.njit.edu/~kas58/mentorDemo/Model/index.php", type;
    EditText title, location, purpose, date, s_time, e_time;
    TextView meeting_with;
    View view;
    Button respond;
    ArrayList<String> meeting_request = new ArrayList<>();
    Toast toast;

    /* When the fragment view is created, initialize necessary objects */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.fragment_meeting_details, container, false);
        title = view.findViewById(R.id.event_title);
        location = view.findViewById(R.id.event_location);
        purpose = view.findViewById(R.id.event_purpose);
        date = view.findViewById(R.id.event_date);
        s_time = view.findViewById(R.id.event_start_time);
        e_time = view.findViewById(R.id.event_end_time);
        meeting_with = view.findViewById(R.id.meeting_user);
        respond = view.findViewById(R.id.respond);
        meeting_request = getActivity().getIntent().getStringArrayListExtra("meeting_details");
        type = getActivity().getIntent().getExtras().getString("type");
        return view;
    }

    @Override
    public void onStart()
    {
        super.onStart();
        /* array = ['id', 'sender', 'receiver', 'title', 'e_date', 'start_time', 'end_time',
                    'location', 'purpose', 'status'] */
        title.setText(meeting_request.get(3));
        location.setText(meeting_request.get(7));
        purpose.setText(meeting_request.get(8));
        date.setText(DateTimeFormat.formatDate(meeting_request.get(4)));
        s_time.setText(DateTimeFormat.format12HourTimeAsString(meeting_request.get(5)));
        e_time.setText(DateTimeFormat.format12HourTimeAsString(meeting_request.get(6)));
        meeting_with.setText(meeting_request.get(2));

        if(meeting_request.get(9).equals("3") && type.equals("receiver"))
        {
            respond.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v) {
                    sendAlert();
                }
            });
        }
        else
            respond.setVisibility(View.GONE);
    }

    /* Sends a web server request to change the meeting request status to approved */

    private void confirmMeeting(View v, String url, final String row_id, final String status)
    {
        RequestQueue queue = Volley.newRequestQueue(v.getContext());
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("Server response: "+response);
                Context context = getContext();
                CharSequence text = "Meeting Confirmed!";
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("Volley Error: " + error);
                error.printStackTrace();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("action", "confirm");
                params.put("row_id", row_id);
                params.put("status", status);
                return params;
            }
        };
        queue.add(request);
    }

    private void sendAlert()
    {
        AlertDialog alert = new AlertDialog.Builder(getActivity()).create();
        alert.setTitle("Response");
        alert.setMessage("Do you wish to accept this meeting request?");
        alert.setButton(android.support.v7.app.AlertDialog.BUTTON_POSITIVE, "Accept",
                new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                confirmMeeting(view, url, meeting_request.get(0), "1");
                respond.setVisibility(View.GONE);
                toast = Toast.makeText(getContext(), "Meeting Accepted", Toast.LENGTH_SHORT);
                toast.show();
                dialog.dismiss();
                getActivity().onBackPressed();
            }
        });

        alert.setButton(android.support.v7.app.AlertDialog.BUTTON_NEGATIVE, "Decline",
                new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                confirmMeeting(view, url, meeting_request.get(0), "2");
                respond.setVisibility(View.GONE);
                toast = Toast.makeText(getContext(), "Meeting Declined", Toast.LENGTH_SHORT);
                toast.show();
                dialog.dismiss();
                getActivity().onBackPressed();
            }
        });

        alert.show();
    }


}