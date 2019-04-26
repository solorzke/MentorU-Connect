package com.example.mentorapp;

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
import android.widget.TextView;

public class SocialCapitalFragment extends Fragment {

    TextView link;
    AlertDialog alert;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_socialcapital, container, false);
        link = (TextView) view.findViewById(R.id.textView22);
        link.setText("Link");
        link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://docs.google.com/document/d/1N7zD74CNZKp-HuRZ6m4Zcj61TKZPBdK5xLgCIVd4S_c/edit";
                Uri u = Uri.parse(url);
                Intent goToDrive = new Intent(Intent.ACTION_VIEW, u);
                if(goToDrive.resolveActivity(getActivity().getPackageManager()) != null){
                    startActivity(goToDrive);
                }
                else{
                    alert = new AlertDialog.Builder(view.getContext()).create();
                    alert.setTitle("Alert");
                    alert.setMessage("No browser detected. Please install an internet browser");
                    alert.setButton(AlertDialog.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener(){
                        public void onClick(DialogInterface dialog, int which){
                            dialog.dismiss();
                        }
                    });
                }
            }
        });

        return view;

    }

    public static WBFragment newInstance(String text) {

        WBFragment f = new WBFragment();
        Bundle b = new Bundle();
        b.putString("msg", text);

        f.setArguments(b);

        return f;
    }


}
