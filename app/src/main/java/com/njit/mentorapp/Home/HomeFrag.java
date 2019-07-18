package com.njit.mentorapp.Home;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.ToxicBakery.viewpager.transforms.CubeOutTransformer;
import com.njit.mentorapp.R;
import com.njit.mentorapp.SideBar;
import com.njit.mentorapp.model.Tools.TabAdapter;

public class HomeFrag extends Fragment {

    private TabAdapter adapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private int[] tabIcons = {
            R.drawable.ic_check_red,
            R.drawable.ic_message_red,
            R.drawable.ic_feed
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_home_page, container, false);
        viewPager = (ViewPager) view.findViewById(R.id.viewPager);
        tabLayout = (TabLayout) view.findViewById(R.id.tabLayout);
        adapter = new TabAdapter(getActivity().getSupportFragmentManager());
        adapter.addFragment(new Goals(), "Goals");
        adapter.addFragment(new MessageFragment(), "Messages");
        adapter.addFragment(new VectorFeedFragment(), "Feed");
        viewPager.setAdapter(adapter);
        viewPager.setPageTransformer(true, new CubeOutTransformer());
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        tabLayout.getTabAt(2).setIcon(tabIcons[2]);
        SideBar bar = (SideBar) getActivity();
        bar.toolbar.setTitle("Home");
        return view;
    }

}