package com.example.mentorapp.Account;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.mentorapp.R;
import com.example.mentorapp.SideBar;
import java.util.HashMap;
import java.util.Map;

public class StudentFragment extends Fragment {

    String url = "https://web.njit.edu/~kas58/mentorDemo/Model/index.php";
    SharedPreferences SESSION, CL, USER_TYPE;
    SharedPreferences.Editor EDITOR, EDITOR2;
    TextView EDIT_CL_BTN, DONE_CL_BTN, EDIT_ACC_BTN, DONE_ACC_BTN, STU_UCID;
    EditText STU_NAME, STU_EMAIL, STU_DEGREE, STU_CLUB, C1_ID, C1_T, C2_ID, C2_T, C3_ID,
            C3_T, C4_ID, C4_T, C5_ID, C5_T, C6_ID, C6_T;
    View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.account_student, container, false);
        SESSION = getActivity().getSharedPreferences("STUDENT", Context.MODE_PRIVATE);
        EDITOR = SESSION.edit();
        CL = getActivity().getSharedPreferences("COURSES", Context.MODE_PRIVATE);
        EDITOR2 = CL.edit();

        EDIT_ACC_BTN = view.findViewById(R.id.edit_acc_btn);
        DONE_ACC_BTN = view.findViewById(R.id.done_acc_btn);
        EDIT_CL_BTN = view.findViewById(R.id.edit_class_btn);
        DONE_CL_BTN = view.findViewById(R.id.done_class_btn);

        STU_NAME = view.findViewById(R.id.acc_stu_name_1);
        STU_UCID = view.findViewById(R.id.acc_stu_ucid_1);
        STU_EMAIL = view.findViewById(R.id.acc_stu_email_1);
        STU_DEGREE = view.findViewById(R.id.acc_stu_degree_1);
        STU_CLUB = view.findViewById(R.id.acc_stu_club_1);
        USER_TYPE = getActivity().getSharedPreferences("USER_TYPE", Context.MODE_PRIVATE);


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

        STU_NAME.setText(SESSION.getString("fname", null) + " " +
                SESSION.getString("lname", null));
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

    @Override
    public void onStart() {
        super.onStart();
        if(isStudent(USER_TYPE)){
            EDIT_ACC_BTN.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EDIT_ACC_BTN.setVisibility(View.INVISIBLE);
                    DONE_ACC_BTN.setVisibility(View.VISIBLE);
                    enableEditAccText(STU_NAME, STU_EMAIL, STU_DEGREE, STU_CLUB);
                }
            });

            DONE_ACC_BTN.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DONE_ACC_BTN.setVisibility(View.INVISIBLE);
                    EDIT_ACC_BTN.setVisibility(View.VISIBLE);
                    disableEditAccText(STU_NAME, STU_EMAIL, STU_DEGREE, STU_CLUB);
                    updateUserSession(EDITOR, STU_NAME, STU_EMAIL, STU_DEGREE, STU_CLUB);
                    sendAccRequest(view, url, "updateRecord", SESSION);
                    postToast("Account Updated");
                }
            });

            EDIT_CL_BTN.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EDIT_CL_BTN.setVisibility(View.INVISIBLE);
                    DONE_CL_BTN.setVisibility(View.VISIBLE);
                    enableEditClassText(C1_ID, C1_T, C2_ID, C2_T, C3_ID,
                            C3_T, C4_ID, C4_T, C5_ID, C5_T, C6_ID, C6_T);

                }
            });

            DONE_CL_BTN.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DONE_CL_BTN.setVisibility(View.INVISIBLE);
                    EDIT_CL_BTN.setVisibility(View.VISIBLE);
                    disableEditClassText(C1_ID, C1_T, C2_ID, C2_T, C3_ID,
                            C3_T, C4_ID, C4_T, C5_ID, C5_T, C6_ID, C6_T);
                    updateClassList(EDITOR2, C1_ID, C1_T, C2_ID, C2_T, C3_ID,
                            C3_T, C4_ID, C4_T, C5_ID, C5_T, C6_ID, C6_T);
                    sendClassRequest(view, url, "updateClasses",CL,
                            SESSION.getString("ucid", null));
                    postToast("Classes Updated");
                }
            });
        }
        else{
            DONE_CL_BTN.setVisibility(View.INVISIBLE);
            EDIT_CL_BTN.setVisibility(View.INVISIBLE);
            EDIT_ACC_BTN.setVisibility(View.INVISIBLE);
            DONE_ACC_BTN.setVisibility(View.INVISIBLE);
        }

    }

    private void disableEditAccText(EditText STU_NAME, EditText STU_EMAIL,
                                    EditText STU_DEGREE, EditText STU_CLUB) {
        STU_NAME.setFocusableInTouchMode(false);
        STU_NAME.setEnabled(false);
        STU_NAME.setCursorVisible(false);
        STU_NAME.setBackgroundColor(Color.TRANSPARENT);

        STU_EMAIL.setFocusableInTouchMode(false);
        STU_EMAIL.setEnabled(false);
        STU_EMAIL.setCursorVisible(false);
        STU_EMAIL.setBackgroundColor(Color.TRANSPARENT);

        STU_DEGREE.setFocusableInTouchMode(false);
        STU_DEGREE.setEnabled(false);
        STU_DEGREE.setCursorVisible(false);
        STU_DEGREE.setBackgroundColor(Color.TRANSPARENT);

        STU_CLUB.setFocusableInTouchMode(false);
        STU_CLUB.setEnabled(false);
        STU_CLUB.setCursorVisible(false);
        STU_CLUB.setBackgroundColor(Color.TRANSPARENT);
    }

    private void enableEditAccText(EditText STU_NAME, EditText STU_EMAIL,
                                   EditText STU_DEGREE, EditText STU_CLUB) {
        STU_NAME.setFocusableInTouchMode(true);
        STU_NAME.setEnabled(true);
        STU_NAME.setCursorVisible(true);
        STU_NAME.setBackgroundColor(Color.TRANSPARENT);

        STU_EMAIL.setFocusableInTouchMode(true);
        STU_EMAIL.setEnabled(true);
        STU_EMAIL.setCursorVisible(true);
        STU_EMAIL.setBackgroundColor(Color.TRANSPARENT);

        STU_DEGREE.setFocusableInTouchMode(true);
        STU_DEGREE.setEnabled(true);
        STU_DEGREE.setCursorVisible(true);
        STU_DEGREE.setBackgroundColor(Color.TRANSPARENT);

        STU_CLUB.setFocusableInTouchMode(true);
        STU_CLUB.setEnabled(true);
        STU_CLUB.setCursorVisible(true);
        STU_CLUB.setBackgroundColor(Color.TRANSPARENT);
    }

    private void disableEditClassText(EditText C1_ID, EditText C1_T, EditText C2_ID, EditText C2_T,
                                 EditText C3_ID, EditText C3_T, EditText C4_ID, EditText C4_T,
                                 EditText C5_ID, EditText C5_T, EditText C6_ID, EditText C6_T) {
        C1_ID.setFocusableInTouchMode(false);
        C1_ID.setEnabled(false);
        C1_ID.setCursorVisible(false);

        C1_T.setFocusableInTouchMode(false);
        C1_T.setEnabled(false);
        C1_T.setCursorVisible(false);

        C2_ID.setFocusableInTouchMode(false);
        C2_ID.setEnabled(false);
        C2_ID.setCursorVisible(false);

        C2_T.setFocusableInTouchMode(false);
        C2_T.setEnabled(false);
        C2_T.setCursorVisible(false);

        C3_ID.setFocusableInTouchMode(false);
        C3_ID.setEnabled(false);
        C3_ID.setCursorVisible(false);

        C3_T.setFocusableInTouchMode(false);
        C3_T.setEnabled(false);
        C3_T.setCursorVisible(false);

        C4_ID.setFocusableInTouchMode(false);
        C4_ID.setEnabled(false);
        C4_ID.setCursorVisible(false);

        C4_T.setFocusableInTouchMode(false);
        C4_T.setEnabled(false);
        C4_T.setCursorVisible(false);

        C5_ID.setFocusableInTouchMode(false);
        C5_ID.setEnabled(false);
        C5_ID.setCursorVisible(false);

        C5_T.setFocusableInTouchMode(false);
        C5_T.setEnabled(false);
        C5_T.setCursorVisible(false);

        C6_ID.setFocusableInTouchMode(false);
        C6_ID.setEnabled(false);
        C6_ID.setCursorVisible(false);

        C6_T.setFocusableInTouchMode(false);
        C6_T.setEnabled(false);
        C6_T.setCursorVisible(false);
    }

    private void enableEditClassText(EditText C1_ID, EditText C1_T, EditText C2_ID, EditText C2_T,
                                EditText C3_ID, EditText C3_T, EditText C4_ID, EditText C4_T,
                                EditText C5_ID, EditText C5_T, EditText C6_ID, EditText C6_T) {
        C1_ID.setFocusableInTouchMode(true);
        C1_ID.setEnabled(true);
        C1_ID.setCursorVisible(true);

        C1_T.setFocusableInTouchMode(true);
        C1_T.setEnabled(true);
        C1_T.setCursorVisible(true);

        C2_ID.setFocusableInTouchMode(true);
        C2_ID.setEnabled(true);
        C2_ID.setCursorVisible(true);

        C2_T.setFocusableInTouchMode(true);
        C2_T.setEnabled(true);
        C2_T.setCursorVisible(true);

        C3_ID.setFocusableInTouchMode(true);
        C3_ID.setEnabled(true);
        C3_ID.setCursorVisible(true);

        C3_T.setFocusableInTouchMode(true);
        C3_T.setEnabled(true);
        C3_T.setCursorVisible(true);

        C4_ID.setFocusableInTouchMode(true);
        C4_ID.setEnabled(true);
        C4_ID.setCursorVisible(true);

        C4_T.setFocusableInTouchMode(true);
        C4_T.setEnabled(true);
        C4_T.setCursorVisible(true);

        C5_ID.setFocusableInTouchMode(true);
        C5_ID.setEnabled(true);
        C5_ID.setCursorVisible(true);

        C5_T.setFocusableInTouchMode(true);
        C5_T.setEnabled(true);
        C5_T.setCursorVisible(true);

        C6_ID.setFocusableInTouchMode(true);
        C6_ID.setEnabled(true);
        C6_ID.setCursorVisible(true);

        C6_T.setFocusableInTouchMode(true);
        C6_T.setEnabled(true);
        C6_T.setCursorVisible(true);

    }

    private void updateUserSession(SharedPreferences.Editor editor, EditText STU_NAME, EditText STU_EMAIL,
                                   EditText STU_DEGREE, EditText STU_CLUB){
        String [] name = STU_NAME.getText().toString().split(" ");
        editor.putString("fname", name[0]);
        editor.putString("lname", name[1]);
        editor.putString("email", STU_EMAIL.getText().toString());
        editor.putString("degree", STU_DEGREE.getText().toString());
        editor.putString("club", STU_CLUB.getText().toString());
        editor.apply();
        SideBar bar = (SideBar) getActivity();
        bar.user_name.setText(name[0] + " " + name[1]);
        bar.user_email.setText(STU_EMAIL.getText().toString());
    }

    private void updateClassList(SharedPreferences.Editor editor, EditText C1_ID, EditText C1_T,
                                 EditText C2_ID, EditText C2_T, EditText C3_ID, EditText C3_T,
                                 EditText C4_ID, EditText C4_T, EditText C5_ID, EditText C5_T,
                                 EditText C6_ID, EditText C6_T){
        editor.putString("id0", C1_ID.getText().toString());
        editor.putString("title0", C1_T.getText().toString());
        editor.putString("id1", C2_ID.getText().toString());
        editor.putString("title1", C2_T.getText().toString());
        editor.putString("id2", C3_ID.getText().toString());
        editor.putString("title2", C3_T.getText().toString());
        editor.putString("id3", C4_ID.getText().toString());
        editor.putString("title3", C4_T.getText().toString());
        editor.putString("id4", C5_ID.getText().toString());
        editor.putString("title4", C5_T.getText().toString());
        editor.putString("id5", C6_ID.getText().toString());
        editor.putString("title5", C6_T.getText().toString());
        editor.apply();
    }

    private void sendAccRequest(View v, String url, final String action, final SharedPreferences ACC){

        RequestQueue queue = Volley.newRequestQueue(v.getContext());
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("Server Response: "+response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("Volley Error: "+error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("action", action);
                params.put("ucid", ACC.getString("ucid", null));
                params.put("fname", ACC.getString("fname", null));
                params.put("lname", ACC.getString("lname", null));
                params.put("email", ACC.getString("email", null));
                params.put("degree", ACC.getString("degree", null));
                params.put("club", ACC.getString("club", null));
                return params;
            }
        };
        queue.add(request);
    }

    private void sendClassRequest(View v, String url, final String action, final SharedPreferences CL, final String ucid){

        RequestQueue queue = Volley.newRequestQueue(v.getContext());
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("Server Response: "+response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("Volley Error: "+error);
            }
        }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("action", action);
                params.put("ucid", ucid);

                params.put("row_id1", CL.getString("row_id0", null));
                params.put("id1", CL.getString("id0", null));
                params.put("title1", CL.getString("title0", null));

                params.put("row_id2", CL.getString("row_id1", null));
                params.put("id2", CL.getString("id1", null));
                params.put("title2", CL.getString("title1", null));

                params.put("row_id3", CL.getString("row_id2", null));
                params.put("id3", CL.getString("id2", null));
                params.put("title3", CL.getString("title2", null));

                params.put("row_id4", CL.getString("row_id3", null));
                params.put("id4", CL.getString("id3", null));
                params.put("title4", CL.getString("title3", null));

                params.put("row_id5", CL.getString("row_id4", null));
                params.put("id5", CL.getString("id4", null));
                params.put("title5", CL.getString("title4", null));

                params.put("row_id6", CL.getString("row_id5", null));
                params.put("id6", CL.getString("id5", null));
                params.put("title6", CL.getString("title5", null));
                return params;
            }

        };
        queue.add(request);
    }

    private void postToast(String msg)
    {
        Context context = getContext();
        CharSequence text = msg;
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    private boolean isStudent(SharedPreferences type)
    {
        if(type.getString("type", null).equals("student"))
        {
            return true;
        }
        else{
            return false;
        }
    }

}