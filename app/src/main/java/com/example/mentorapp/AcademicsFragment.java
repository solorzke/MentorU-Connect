package com.example.mentorapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.mentorapp.Academic.Article1;
import com.example.mentorapp.WellBeing.WB_Article1;

public class AcademicsFragment extends Fragment {

    TextView link;
    AlertDialog alert;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_academics, container, false);
        link = view.findViewById(R.id.textView22);
        link.setText("View Resources");

        link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://docs.google.com/document/d/1lFDVsojt0_2IVSpJsC_qhrCnKb9ur6QA3FT5tskwQog/edit?usp=sharing";
                Uri forgotPw = Uri.parse(url);
                Intent goToForgotPw = new Intent(Intent.ACTION_VIEW, forgotPw);
                if(goToForgotPw.resolveActivity(getActivity().getPackageManager()) != null){
                    startActivity(goToForgotPw);
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
