package com.example.mentorapp;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.mentorapp.WellBeing.WB_Article1;

public class WBFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_wb, container, false);

        RelativeLayout ARTICLE_1 = view.findViewById(R.id.rl1);
        RelativeLayout ARTICLE_2 = view.findViewById(R.id.rl2);
        RelativeLayout ARTICLE_3 = view.findViewById(R.id.rl3);
        RelativeLayout ARTICLE_4 = view.findViewById(R.id.rl4);

        /* Clicking the first article */
        ARTICLE_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent a1 = new Intent(view.getContext(), WB_Article1.class);
                startActivity(a1);
            }
        });

        /* Clicking the second article */
        ARTICLE_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent a2 = new Intent(view.getContext(), WB_Article1.class);
                startActivity(a2);
            }
        });

        /* Clicking the third article */
        ARTICLE_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent a3 = new Intent(view.getContext(), WB_Article1.class);
                startActivity(a3);
            }
        });

        /* Clicking the fourth article */
        ARTICLE_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent a4 = new Intent(view.getContext(), WB_Article1.class);
                startActivity(a4);
            }
        });

        return view;

    }


}
