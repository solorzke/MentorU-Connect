package com.example.mentorapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.example.mentorapp.Account.MentorFragment;
import com.example.mentorapp.Account.StudentFragment;
import com.example.mentorapp.Events.MeetingDetails;
import com.example.mentorapp.Events.MeetingStatus;
import com.example.mentorapp.model.TabAdapter;

import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class AccountFragment extends Fragment {

    private TabAdapter adapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private int[] tabIcons = {
            R.drawable.ic_student,
            R.drawable.ic_mentor,
    };
    TextView cancel;
    String url = "https://web.njit.edu/~kas58/mentorDemo/Model/index.php";
    SharedPreferences SESSION;
    Timer timer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_account, container, false);

        viewPager = (ViewPager) view.findViewById(R.id.viewPager);
        tabLayout = (TabLayout) view.findViewById(R.id.tabLayout);
        adapter = new TabAdapter(getActivity().getSupportFragmentManager());
        //adapter.addFragment(new StudentFragment(), "Student");
        //adapter.addFragment(new MentorFragment(), "Mentor");
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);

        return view;

    }

}