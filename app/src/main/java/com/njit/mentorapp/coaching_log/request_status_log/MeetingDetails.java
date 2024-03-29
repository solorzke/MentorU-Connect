package com.njit.mentorapp.coaching_log.request_status_log;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
import com.njit.mentorapp.model.service.NotificationText;
import com.njit.mentorapp.model.service.PushMessageToFCM;
import com.njit.mentorapp.R;
import com.njit.mentorapp.model.tools.DateTimeFormat;
import com.njit.mentorapp.model.service.WebServer;
import com.njit.mentorapp.model.users.Mentee;
import com.njit.mentorapp.model.users.Mentor;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class MeetingDetails extends Fragment
{
    String type, responder;
    String [] notifyResponse;
    EditText title, location, purpose, date, s_time, e_time;
    TextView meeting_with;
    View view;
    Button respond;
    ArrayList<String> meeting_request = new ArrayList<>();
    Toast toast;
    boolean done = false;

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
        responder = getActivity().getIntent().getExtras().getString("responder");
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
        String party = meeting_request.get(2) + " & " + meeting_request.get(1);
        meeting_with.setText(party);

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

    private void confirmMeeting(final View v, String url, final String row_id, final String status)
    {
        RequestQueue queue = Volley.newRequestQueue(v.getContext());
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("DEBUG_OUTPUT","Server Response: "+response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("DEBUG_OUTPUT","Volley Error: " + error);
                error.printStackTrace();
                if(error instanceof TimeoutError)
                    Toast.makeText(
                            v.getContext(),
                            "Request timed out. Check your network settings.",
                            Toast.LENGTH_SHORT
                    ).show();

                else if(error instanceof NetworkError)
                    Toast.makeText(
                            v.getContext(),
                            "Can't connect to the internet",
                            Toast.LENGTH_SHORT
                    ).show();
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
        request.setRetryPolicy(new DefaultRetryPolicy(
                3000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        );
    }

    private void sendAlert()
    {
        AlertDialog alert = new AlertDialog.Builder(getActivity()).create();
        alert.setTitle("Response");
        alert.setMessage("Do you wish to accept this meeting request?");
        alert.setButton(AlertDialog.BUTTON_POSITIVE, "Accept",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        confirmMeeting(view, WebServer.getQueryLink(), meeting_request.get(0), "1");
                        notifyResponse = NotificationText.meetingResponse(responder, "accepted");
                        PushMessageToFCM.send(getContext(), new Mentor(view.getContext()).getTopicID(), notifyResponse[0], notifyResponse[1]);
                        respond.setVisibility(View.GONE);
                        toast = Toast.makeText(getContext(), "Meeting Accepted", Toast.LENGTH_SHORT);
                        toast.show();
                        dialog.dismiss();
                        createEvent(meeting_request);
                    }
                }
        );

        alert.setButton(AlertDialog.BUTTON_NEGATIVE, "Decline",
                new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                confirmMeeting(view, WebServer.getQueryLink(), meeting_request.get(0), "2");
                notifyResponse = NotificationText.meetingResponse(responder, "declined");
                PushMessageToFCM.send(getContext(), new Mentor(view.getContext()).getTopicID(), notifyResponse[0], notifyResponse[1]);
                respond.setVisibility(View.GONE);
                toast = Toast.makeText(getContext(), "Meeting Declined", Toast.LENGTH_SHORT);
                toast.show();
                dialog.dismiss();
                getActivity().onBackPressed();
            }
        });

        alert.show();
    }

    /* Function to create the Calendar event using its API. */
    private void createEvent(ArrayList <String> details)
    {
        /* array = ['id', 'sender', 'receiver', 'title', 'e_date', 'start_time', 'end_time',
                    'location', 'purpose', 'status'] */
        String formatted_date = DateTimeFormat.formatDate(details.get(4));
        int [] s_formatTime = DateTimeFormat.format24HourTime(details.get(5));
        int [] e_formatTime = DateTimeFormat.format24HourTime(details.get(6));
        int [] s_timestamp = DateTimeFormat.parseDateAndTime(formatted_date, s_formatTime[0], s_formatTime[1]);
        int [] e_timestamp = DateTimeFormat.parseDateAndTime(formatted_date, e_formatTime[0], e_formatTime[1]);

        /* Convert date/time to milliseconds */
        Calendar beginTime = Calendar.getInstance();
        beginTime.set(s_timestamp[0], s_timestamp[1] -= 1, s_timestamp[2], s_timestamp[3], s_timestamp[4]);
        long startMillis = beginTime.getTimeInMillis();

        Calendar endTime = Calendar.getInstance();
        endTime.set(e_timestamp[0], e_timestamp[1] -= 1, e_timestamp[2], e_timestamp[3], e_timestamp[4]);
        long endMillis = endTime.getTimeInMillis();

        /* Pass a URI Intent to the Calendar API along with the form data, to take you to its interface */
        Intent intent = new Intent(Intent.ACTION_INSERT)
                .setData(CalendarContract.Events.CONTENT_URI)
                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startMillis)
                .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endMillis)
                .putExtra(CalendarContract.Events.TITLE, details.get(3))
                .putExtra(CalendarContract.Events.DESCRIPTION, details.get(8))
                .putExtra(CalendarContract.Events.EVENT_LOCATION, details.get(7))
                .putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY)
                .putExtra(Intent.EXTRA_EMAIL, getEmail());
        this.done = true;
        startActivity(intent);
        getActivity().onBackPressed();
    }

    private String getEmail()
    {
        if(meeting_with.getText().toString().equals(new Mentor(view.getContext()).getEmail()))
            return new Mentor(view.getContext()).getEmail();
        else
            return new Mentee(view.getContext()).getEmail();
    }



}