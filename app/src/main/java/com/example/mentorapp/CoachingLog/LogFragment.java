package com.example.mentorapp.CoachingLog;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.example.mentorapp.R;

import java.util.ArrayList;

public class LogFragment extends Fragment
{
    ListView listView;
    ArrayList <String> array;
    ArrayAdapter adapter;
    View view;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.fragment_coaching_log, container, false);
        listView = view.findViewById(R.id.list);
        array = new ArrayList<String>();
        array.add("Discuss importance of good resumes");
        array.add("Discover your ambitions");
        array.add("Review your end-of-semester experience at NJIT.");
        adapter = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1, array);
        listView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onStart()
    {
        super.onStart();
    }
}
