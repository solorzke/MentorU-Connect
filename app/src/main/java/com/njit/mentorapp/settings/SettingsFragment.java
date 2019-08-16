package com.njit.mentorapp.settings;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.njit.mentorapp.account.Courses;
import com.njit.mentorapp.account.MentorActivity;
import com.njit.mentorapp.account.MenteeActivity;
import com.njit.mentorapp.R;
import com.njit.mentorapp.sidebar.SideBar;
import com.njit.mentorapp.model.users.Mentee;

public class SettingsFragment extends Fragment implements View.OnClickListener
{
    private View view;
    private LinearLayout mentor, mentee, courses, office, terms, privacy, about, delete_acc;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.fragment_settings, container, false);
        mentor = view.findViewById(R.id.mentor);
        mentee = view.findViewById(R.id.mentee);
        courses = view.findViewById(R.id.courses);
        office = view.findViewById(R.id.office);
        terms = view.findViewById(R.id.terms);
        privacy = view.findViewById(R.id.privacy);
        about = view.findViewById(R.id.about);
        delete_acc = view.findViewById(R.id.delete_acc);
        SideBar bar = (SideBar) getActivity();
        if(bar != null)
            bar.toolbar.setTitle("Settings");
        return view;
    }

    @Override
    public void onStart()
    {
        super.onStart();
        mentor.setOnClickListener(this);
        mentee.setOnClickListener(this);
        courses.setOnClickListener(this);
        office.setOnClickListener(this);
        terms.setOnClickListener(this);
        privacy.setOnClickListener(this);
        about.setOnClickListener(this);
        delete_acc.setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.mentee:
                if(!new Mentee(view.getContext()).notRegistered())
                    startActivity(new Intent(getContext(), MenteeActivity.class));
                else
                    Toast.makeText(
                            view.getContext(),
                            "Mentee Information not available",
                            Toast.LENGTH_SHORT
                    ).show();
                break;

            case R.id.courses:
                if(!new Mentee(view.getContext()).notRegistered())
                    startActivity(new Intent(getContext(), Courses.class));
                else
                    Toast.makeText(
                            view.getContext(),
                            "Mentee Information not available",
                            Toast.LENGTH_SHORT
                    ).show();
                break;

            case R.id.mentor:
                startActivity(new Intent(getContext(), MentorActivity.class));
                break;

            case R.id.delete_acc:
                startActivity(new Intent(view.getContext(), DeleteAccountActivity.class));
                break;

            case R.id.office:
                startActivity(new Intent(getContext(), ContactOffice.class));
                break;

            case R.id.about:
                startActivity(new Intent(getContext(), AboutUs.class));
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
