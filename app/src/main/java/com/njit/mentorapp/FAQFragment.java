package com.njit.mentorapp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class FAQFragment extends Fragment
{
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        final View view = inflater.inflate(R.layout.fragment_faq, container, false);
        SideBar side = (SideBar) getActivity();
        side.toolbar.setTitle("FAQ");
        return view;
    }
}
