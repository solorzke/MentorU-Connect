package com.example.mentorapp.Home;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.mentorapp.AccountFragment;
import com.example.mentorapp.FAB.EditMessage;
import com.example.mentorapp.FAQFragment;
import com.example.mentorapp.R;
import com.example.mentorapp.Report.ReportActivity;
import com.example.mentorapp.SendEmail;
import com.example.mentorapp.SideBar;
import com.example.mentorapp.model.DateTimeFormat;
import java.util.HashMap;
import java.util.Map;

public class MessageFragment extends Fragment implements View.OnClickListener {

    SharedPreferences STUDENT, MENTOR, USER_TYPE;
    FloatingActionButton FAB;
    TextView FEEDBACK, messenger, date, contact_user, account_info, help_center, report;
    ImageView messenger_img, thumbs_up, thumbs_down;
    String url = "https://web.njit.edu/~kas58/mentorDemo/Model/index.php";
    View view;
    boolean up = false, down = false;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_message, container, false);
        STUDENT = getActivity().getSharedPreferences("STUDENT", Context.MODE_PRIVATE);
        MENTOR = getActivity().getSharedPreferences("MENTOR", Context.MODE_PRIVATE);
        USER_TYPE = getActivity().getSharedPreferences("USER_TYPE", Context.MODE_PRIVATE);
        FAB = view.findViewById(R.id.m_fab);
        messenger_img = view.findViewById(R.id.messenger_img);
        messenger = view.findViewById(R.id.messenger);
        date = view.findViewById(R.id.date);
        thumbs_up = view.findViewById(R.id.thumbsup);
        thumbs_down = view.findViewById(R.id.thumbsdown);
        contact_user = view.findViewById(R.id.contact_user);
        account_info = view.findViewById(R.id.acc_info);
        help_center = view.findViewById(R.id.help);
        FEEDBACK = view.findViewById(R.id.feedback);
        report = view.findViewById(R.id.report);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        FAB.setOnClickListener(this);
        thumbs_up.setOnClickListener(this);
        thumbs_down.setOnClickListener(this);
        contact_user.setOnClickListener(this);
        account_info.setOnClickListener(this);
        help_center.setOnClickListener(this);
        report.setOnClickListener(this);

        if(isStudent(USER_TYPE)){
            String fname = MENTOR.getString("fname", null);
            String lname = MENTOR.getString("lname", null);
            messenger.setText(fname + " " + lname);
            getMessage(STUDENT, MENTOR, view);
        }
        else{
            String fname = STUDENT.getString("fname", null);
            String lname = STUDENT.getString("lname", null);
            messenger.setText(fname + " " + lname);
            getMessage(MENTOR, STUDENT, view);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(isStudent(USER_TYPE)){
            String fname = MENTOR.getString("fname", null);
            String lname = MENTOR.getString("lname", null);
            messenger.setText(fname + " " + lname);
            getMessage(STUDENT, MENTOR, view);
        }
        else{
            String fname = STUDENT.getString("fname", null);
            String lname = STUDENT.getString("lname", null);
            messenger.setText(fname + " " + lname);
            getMessage(MENTOR, STUDENT, view);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(isStudent(USER_TYPE)){
            String fname = MENTOR.getString("fname", null);
            String lname = MENTOR.getString("lname", null);
            messenger.setText(fname + " " + lname);
            getMessage(STUDENT, MENTOR, view);
        }
        else{
            String fname = STUDENT.getString("fname", null);
            String lname = STUDENT.getString("lname", null);
            messenger.setText(fname + " " + lname);
            getMessage(MENTOR, STUDENT, view);
        }
    }

    private boolean isStudent(SharedPreferences type) {
        if (type.getString("type", null).equals("student")) {
            return true;
        } else {
            return false;
        }
    }

    private void getMessage(final SharedPreferences RECEIVER, final SharedPreferences SENDER, View view){
        RequestQueue queue = Volley.newRequestQueue(view.getContext());
        StringRequest stringRequest_1 = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println(response);
                        String [] reply = response.split("\\|");
                        if (reply[0].equals("empty")) {
                            FEEDBACK.setText("No new feedback from " + SENDER.getString("fname", null));
                            date.setText("Date: N/A");

                        } else {
                            FEEDBACK.setText(reply[0]);
                            String d = DateTimeFormat.formatDate(reply[1]);
                            date.setText("Date: " + d);
                            setLiking(reply[2]);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("action", "getFeedback");
                params.put("receiver", RECEIVER.getString("ucid", null));
                params.put("sender", SENDER.getString("ucid", null));  //Change this later <------

                return params;
            }
        };

        queue.add(stringRequest_1);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.m_fab:
                startActivity(new Intent(getActivity(), EditMessage.class));
                break;

            case R.id.thumbsup:
                if(up){
                    up = false;
                    thumbs_up.setImageResource(R.drawable.ic_thumb_up);
                    likeOrDislike(STUDENT, MENTOR, view, "0", FEEDBACK);
                }
                else{
                    up = true;
                    down = false;
                    thumbs_down.setImageResource(R.drawable.ic_thumb_down);
                    thumbs_up.setImageResource(R.drawable.ic_thumb_up_green);
                    postToast("up");
                    likeOrDislike(STUDENT, MENTOR, view, "1", FEEDBACK);
                }
                break;

            case R.id.thumbsdown:
                if(down){
                    down = false;
                    thumbs_down.setImageResource(R.drawable.ic_thumb_down);
                    likeOrDislike(STUDENT, MENTOR, view, "0", FEEDBACK);
                }
                else{
                    down = true;
                    up = false;
                    thumbs_up.setImageResource(R.drawable.ic_thumb_up);
                    thumbs_down.setImageResource(R.drawable.ic_thumb_down_red);
                    postToast("down");
                    likeOrDislike(STUDENT, MENTOR, view, "2", FEEDBACK);
                }
                break;

            case R.id.contact_user:
                startActivity(new Intent(getActivity(), SendEmail.class));
                break;

            case R.id.acc_info:
                Fragment account = new AccountFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, account);
                transaction.addToBackStack(null);
                // Commit the transaction
                transaction.commit();
                SideBar.navigationView.setCheckedItem(R.id.account);
                break;

            case R.id.help:
                Fragment help = new FAQFragment();
                FragmentTransaction transaction2 = getFragmentManager().beginTransaction();
                transaction2.replace(R.id.fragment_container, help);
                transaction2.addToBackStack(null);
                // Commit the transaction
                transaction2.commit();
                SideBar.navigationView.setCheckedItem(R.id.faq);
                break;

            case R.id.report:
                startActivity(new Intent(getActivity(), ReportActivity.class));
                break;

            default:
                break;
        }
    }

    private void postToast(String type) {

        if (type.equals("up")) {
            Context context = getContext();
            CharSequence text = "Liked this post!";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        } else if (type.equals("down")) {
            Context context = getContext();
            CharSequence text = "Disliked this post!";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }

    }

    private void likeOrDislike(final SharedPreferences STUDENT, final SharedPreferences MENTOR, View view,
                               final String status, final TextView msg){
        RequestQueue queue = Volley.newRequestQueue(view.getContext());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("action", "liking");
                params.put("student", STUDENT.getString("ucid", null));
                params.put("mentor", MENTOR.getString("ucid", null));
                params.put("msg", msg.getText().toString());
                params.put("status", status);

                return params;
            }
        };

        queue.add(stringRequest);
    }

    private void setLiking(String status){
        if(status.equals("0")){
            this.thumbs_up.setImageResource(R.drawable.ic_thumb_up);
            this.thumbs_down.setImageResource(R.drawable.ic_thumb_down);
            this.up = false;
            this.down = false;
        }
        else if(status.equals("1")){
            this.thumbs_up.setImageResource(R.drawable.ic_thumb_up_green);
            this.thumbs_down.setImageResource(R.drawable.ic_thumb_down);
            this.up = true;
            this.down = false;
        }

        else if(status.equals("2")){
            this.thumbs_up.setImageResource(R.drawable.ic_thumb_up);
            this.thumbs_down.setImageResource(R.drawable.ic_thumb_down_red);
            this.up = false;
            this.down = true;
        }
    }
}

