package com.njit.mentorapp.Settings;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Switch;

import com.njit.mentorapp.Account.Courses;
import com.njit.mentorapp.Account.MentorActivity;
import com.njit.mentorapp.Account.StudentActivity;
import com.njit.mentorapp.R;
import com.njit.mentorapp.SideBar;

public class SettingsFragment extends Fragment implements View.OnClickListener
{
    View view;
    LinearLayout mentor, mentee, courses, office, terms, privacy;
    Switch notifications;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.fragment_settings, container, false);
        notifications = view.findViewById(R.id.switcher);
        mentor = view.findViewById(R.id.mentor);
        mentee = view.findViewById(R.id.mentee);
        courses = view.findViewById(R.id.courses);
        office = view.findViewById(R.id.office);
        terms = view.findViewById(R.id.terms);
        privacy = view.findViewById(R.id.privacy);
        SideBar bar = (SideBar) getActivity();
        bar.toolbar.setTitle("Settings");
        return view;
    }

    @Override
    public void onStart()
    {
        super.onStart();
        notifications.setOnClickListener(this);
        mentor.setOnClickListener(this);
        mentee.setOnClickListener(this);
        courses.setOnClickListener(this);
        office.setOnClickListener(this);
        terms.setOnClickListener(this);
        privacy.setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.switcher:
                /* Toggle the swticher on or off
                * Notifications aren't set yet, come back later. */
                break;

            case R.id.mentee:
                startActivity(new Intent(getContext(), StudentActivity.class));
                break;

            case R.id.courses:
                startActivity(new Intent(getContext(), Courses.class));
                break;

            case R.id.mentor:
                startActivity(new Intent(getContext(), MentorActivity.class));
                break;

            case R.id.office:
                startActivity(new Intent(getContext(), ContactOffice.class));
                break;

            case R.id.terms:
                startActivity(new Intent(getContext(), TermsAndConditions.class));
                break;

            case R.id.privacy:
                startActivity(new Intent(getContext(), PrivacyPolicy.class));
                break;

            default:
                break;
        }
    }
}
