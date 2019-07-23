package com.njit.mentorapp.coaching_log.request_status_log;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.njit.mentorapp.R;
import com.github.ybq.android.spinkit.SpinKitView;
import java.util.ArrayList;
import cdflynn.android.library.checkview.CheckView;

public class MeetingStatus extends Fragment
{
    View view;
    TextView t1, t2;
    SpinKitView spin;
    ArrayList <String> meeting_details = new ArrayList<>();
    RelativeLayout accepted, declined;
    CheckView check;
    String status;
    /* When the fragment view is created, initialize necessary objects */
    /* array = ['id', 'sender', 'receiver', 'title', 'e_date', 'start_time', 'end_time',
                    'location', 'purpose', 'status'] */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.fragment_request_status, container, false);
        t1 = view.findViewById(R.id.status_title);
        t2 = view.findViewById(R.id.status_title2);
        spin = view.findViewById(R.id.spin_kit);
        accepted = view.findViewById(R.id.accepted_layout);
        declined = view.findViewById(R.id.declined_layout);
        meeting_details = getActivity().getIntent().getStringArrayListExtra("meeting_details");
        check = view.findViewById(R.id.check);
        status = meeting_details.get(9);
        return view;
    }

    @Override
    public void onStart()
    {
        super.onStart();
        checkStatus();
    }

    /* When you come back into focus of the fragment, resend a status check request. */
    @Override
    public void onResume()
    {
        super.onResume();
        checkStatus();
    }

    @Override
    public void onPause()
    {
        super.onPause();
        checkStatus();
    }

    private void checkStatus()
    {
        String type = getActivity().getIntent().getExtras().getString("type");
        switch (status)
        {
            /* Request was accepted */
            case "1":
                spin.setVisibility(View.GONE);
                accepted.setVisibility(View.VISIBLE);
                check.check();
                if (type.equals("sender"))
                {
                    t1.setText("Your meeting request was accepted.");
                    t2.setText("View the meeting info.");
                }
                else if (type.equals("receiver"))
                {
                    t1.setText("You've accepted the meeting request.");
                    t2.setText("View the details on the next page.");
                }
                break;

            /* Request was declined */
            case "2":
                spin.setVisibility(View.GONE);
                declined.setVisibility(View.VISIBLE);
                if (type.equals("sender"))
                {
                    t1.setText("The meeting request was canceled.");
                    t2.setText("Schedule a new meeting.");
                }
                else if (type.equals("receiver"))
                {
                    t1.setText("You canceled the meeting request.");
                    t2.setText("Schedule a new one!");
                }
                break;

            /* Request is pending */
            case "3":
                if (type.equals("sender"))
                {
                    t1.setText("Your request is waiting to be approved!");
                    t2.setText("Check back later.");
                }
                else if (type.equals("receiver"))
                {
                    t1.setText("You've received a meeting request!");
                    t2.setText("Check the details.");
                }
                break;

            default:
                break;
        }
    }
}