package com.example.mentorapp.CoachingLog;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.example.mentorapp.R;

public class LogFragment extends Fragment implements View.OnClickListener
{
    LinearLayout request, status, logs, howTo;
    View view;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.fragment_coaching_log, container, false);
        request = view.findViewById(R.id.requestmeeting);
        status = view.findViewById(R.id.status);
        logs = view.findViewById(R.id.logs);
        howTo = view.findViewById(R.id.howto);
        return view;
    }

    @Override
    public void onStart()
    {
        super.onStart();
        request.setOnClickListener(this);
        status.setOnClickListener(this);
        logs.setOnClickListener(this);
        howTo.setOnClickListener(this);
    }

    /* Create a new intent and head to the page, given the click */
    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.requestmeeting:
                startActivity(new Intent(getContext(), Activity.class));
                break;

            case R.id.status:
                startActivity(new Intent(getContext(), Activity.class));
                break;

            case R.id.logs:
                startActivity(new Intent(getContext(), Activity.class));
                break;

            case R.id.howto:
                startActivity(new Intent(getContext(), Activity.class));
                break;

            default:
                break;
        }
    }
}
