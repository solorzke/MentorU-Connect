package com.njit.mentorapp.sidebar;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.njit.mentorapp.R;

public class FAQFragment extends Fragment
{
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        final View view = inflater.inflate(R.layout.fragment_faq, container, false);
        SideBar side = (SideBar) getActivity();
        if(side != null)
            side.toolbar.setTitle("FAQ");
        return view;
    }
}
