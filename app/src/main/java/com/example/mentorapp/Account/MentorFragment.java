package com.example.mentorapp.Account;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mentorapp.R;
import com.squareup.picasso.Picasso;

public class MentorFragment extends Fragment {

    SharedPreferences MENTOR;
    ImageView AVI;
    TextView MTR_NAME, MTR_EMAIL, MTR_UCID, MTR_DATE, MTR_DEGREE, MTR_OCC, MTR_MENTEE;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.account_mentor, container, false);
        MENTOR = getActivity().getSharedPreferences("MENTOR", Context.MODE_PRIVATE);

        AVI = view.findViewById(R.id.picasso);
        MTR_NAME = view.findViewById(R.id.acc_mtr_name);
        MTR_EMAIL = view.findViewById(R.id.acc_mtr_email);
        MTR_UCID = view.findViewById(R.id.mtr_ucid1);
        MTR_DATE = view.findViewById(R.id.mtr_date1);
        MTR_DEGREE = view.findViewById(R.id.mtr_degree1);
        MTR_OCC = view.findViewById(R.id.mtr_occ1);
        MTR_MENTEE = view.findViewById(R.id.mtr_mentee1);

        Picasso.get().load(MENTOR.getString("avi", null)).into(AVI);
        MTR_NAME.setText(MENTOR.getString("fname", null) + " " + MENTOR.getString(
                "lname", null));
        MTR_EMAIL.setText(MENTOR.getString("email", null));
        MTR_UCID.setText(MENTOR.getString("ucid", null));
        MTR_DATE.setText(MENTOR.getString("grad_date", null));
        MTR_DEGREE.setText(MENTOR.getString("degree", null));
        MTR_OCC.setText(MENTOR.getString("occupation", null));
        MTR_MENTEE.setText(MENTOR.getString("mentee", null));

        return view;
    }
}