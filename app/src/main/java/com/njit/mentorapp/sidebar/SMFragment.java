package com.njit.mentorapp.sidebar;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.njit.mentorapp.R;
import com.njit.mentorapp.sidebar.SideBar;

public class SMFragment extends Fragment {

    RelativeLayout FB, TWITTER, IG, REDDIT;
    AlertDialog alert;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_sm, container, false);
        FB = view.findViewById(R.id.rl_facebook);
        IG = view.findViewById(R.id.rl_ig);
        TWITTER = view.findViewById(R.id.rl_twitter);
        REDDIT = view.findViewById(R.id.rl_reddit);
        SideBar side = (SideBar) getActivity();
        side.toolbar.setTitle("Social Media");

        FB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://www.facebook.com/NewJerseyInstituteofTechnology/";
                Uri link = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW, link);
                if(intent.resolveActivity(getActivity().getPackageManager()) != null){
                    startActivity(intent);
                }
                else{
                    alert = new AlertDialog.Builder(view.getContext()).create();
                    alert.setTitle("Alert");
                    alert.setMessage("No browser detected. Please install an internet browser");
                    alert.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener(){
                        public void onClick(DialogInterface dialog, int which){
                            dialog.dismiss();
                        }
                    });
                }
            }
        });

        IG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://www.instagram.com/instanjit/";
                Uri link = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW, link);
                if(intent.resolveActivity(getActivity().getPackageManager()) != null){
                    startActivity(intent);
                }
                else{
                    alert = new AlertDialog.Builder(view.getContext()).create();
                    alert.setTitle("Alert");
                    alert.setMessage("No browser detected. Please install an internet browser");
                    alert.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener(){
                        public void onClick(DialogInterface dialog, int which){
                            dialog.dismiss();
                        }
                    });
                }
            }
        });

        TWITTER.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://twitter.com/NJIT";
                Uri link = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW, link);
                if(intent.resolveActivity(getActivity().getPackageManager()) != null){
                    startActivity(intent);
                }
                else{
                    alert = new AlertDialog.Builder(view.getContext()).create();
                    alert.setTitle("Alert");
                    alert.setMessage("No browser detected. Please install an internet browser");
                    alert.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener(){
                        public void onClick(DialogInterface dialog, int which){
                            dialog.dismiss();
                        }
                    });
                }
            }
        });

        REDDIT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://www.reddit.com/r/NJTech/";
                Uri link = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW, link);
                if(intent.resolveActivity(getActivity().getPackageManager()) != null){
                    startActivity(intent);
                }
                else{
                    alert = new AlertDialog.Builder(view.getContext()).create();
                    alert.setTitle("Alert");
                    alert.setMessage("No browser detected. Please install an internet browser");
                    alert.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener(){
                        public void onClick(DialogInterface dialog, int which){
                            dialog.dismiss();
                        }
                    });
                }
            }
        });

        return view;
    }
}