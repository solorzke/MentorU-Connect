package com.example.mentorapp.Account;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.TextView;

import com.example.mentorapp.R;
import com.example.mentorapp.WellBeing.WB_Article1;

import java.util.Set;

public class StudentFragment extends Fragment {

    SharedPreferences SESSION, CL;
    TextView STU_NAME, STU_UCID, STU_EMAIL, STU_DEGREE, STU_CLUB, C1_ID, C1_T, C2_ID, C2_T, C3_ID,
            C3_T, C4_ID, C4_T, C5_ID, C5_T, C6_ID, C6_T;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.account_student, container, false);
        SESSION = getActivity().getSharedPreferences("USER", Context.MODE_PRIVATE);
        CL = getActivity().getSharedPreferences("COURSES", Context.MODE_PRIVATE);

        STU_NAME = view.findViewById(R.id.acc_stu_name_1);
        STU_UCID = view.findViewById(R.id.acc_stu_ucid_1);
        STU_EMAIL = view.findViewById(R.id.acc_stu_email_1);
        STU_DEGREE = view.findViewById(R.id.acc_stu_degree_1);
        STU_CLUB = view.findViewById(R.id.acc_stu_club_1);
        C1_ID = view.findViewById(R.id.class_id_1);
        C1_T = view.findViewById(R.id.class_title_1);
        C2_ID = view.findViewById(R.id.class_id_2);
        C2_T = view.findViewById(R.id.class_title_2);
        C3_ID = view.findViewById(R.id.class_id_3);
        C3_T = view.findViewById(R.id.class_title_3);
        C4_ID = view.findViewById(R.id.class_id_4);
        C4_T = view.findViewById(R.id.class_title_4);
        C5_ID = view.findViewById(R.id.class_id_5);
        C5_T = view.findViewById(R.id.class_title_5);
        C6_ID = view.findViewById(R.id.class_id_6);
        C6_T = view.findViewById(R.id.class_title_6);

        STU_NAME.setText(SESSION.getString("fname", null) + " " + SESSION.getString("lname", null));
        STU_UCID.setText(SESSION.getString("ucid", null));
        STU_EMAIL.setText(SESSION.getString("email", null));
        STU_DEGREE.setText(SESSION.getString("degree", null));
        STU_CLUB.setText(SESSION.getString("club", null));

        C1_ID.setText(CL.getString("id0", null));
        C1_T.setText(CL.getString("title0", null));
        C2_ID.setText(CL.getString("id1", null));
        C2_T.setText(CL.getString("title1", null));
        C3_ID.setText(CL.getString("id2", null));
        C3_T.setText(CL.getString("title2", null));
        C4_ID.setText(CL.getString("id3", null));
        C4_T.setText(CL.getString("title3", null));
        C5_ID.setText(CL.getString("id4", null));
        C5_T.setText(CL.getString("title4", null));
        C6_ID.setText(CL.getString("id5", null));
        C6_T.setText(CL.getString("title5", null));

        return view;
    }
}