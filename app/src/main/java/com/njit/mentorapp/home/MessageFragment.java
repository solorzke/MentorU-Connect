package com.njit.mentorapp.home;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.njit.mentorapp.account.MentorActivity;
import com.njit.mentorapp.account.StudentActivity;
import com.njit.mentorapp.fab.EditMessage;
import com.njit.mentorapp.sidebar.FAQFragment;
import com.njit.mentorapp.model.service.NotificationText;
import com.njit.mentorapp.model.service.PushMessageToFCM;
import com.njit.mentorapp.R;
import com.njit.mentorapp.model.users.Mentee;
import com.njit.mentorapp.model.users.Mentor;
import com.njit.mentorapp.report.ReportActivity;
import com.njit.mentorapp.toolbar.SendEmail;
import com.njit.mentorapp.sidebar.SideBar;
import com.njit.mentorapp.model.tools.DateTimeFormat;
import com.njit.mentorapp.model.service.WebServer;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class MessageFragment extends Fragment implements View.OnClickListener
{
    SharedPreferences USER_TYPE;
    String [] notifyLikesText, notifyDislikesText;
    FloatingActionButton FAB;
    TextView FEEDBACK, messenger, date, contact_user, account_info, help_center, report;
    ImageView thumbs_up, thumbs_down, share;
    CircularImageView messenger_img;
    View view;
    private Mentor mentor;
    private Mentee mentee;
    private ConstraintLayout bar;
    boolean up = false, down = false;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.fragment_message, container, false);
        if(getActivity() != null)
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
        share = view.findViewById(R.id.share);
        bar = view.findViewById(R.id.bar);
        mentor = new Mentor(view.getContext());
        mentee = new Mentee(view.getContext());
        return view;
    }

    @Override
    public void onStart()
    {
        super.onStart();
        FAB.setOnClickListener(this);
        thumbs_up.setOnClickListener(this);
        thumbs_down.setOnClickListener(this);
        contact_user.setOnClickListener(this);
        account_info.setOnClickListener(this);
        help_center.setOnClickListener(this);
        report.setOnClickListener(this);
        share.setOnClickListener(this);

        if(isStudent(USER_TYPE))
        {
            String full_name = mentor.getFname() + " " + mentor.getLname();
            String ucid = mentee.getUcid();
            messenger.setText(full_name);
            if(!mentor.getAvi().equals("N/A"))
                Picasso.get().load(mentor.getAvi()).into(messenger_img);
            notifyLikesText = NotificationText.likes(ucid);
            notifyDislikesText = NotificationText.dislikes(ucid);
            getMessage(mentee.getUcid(), mentor.getUcid(), view);
        }
        else
        {
            String full_name = mentee.getFname() + " " + mentee.getLname();
            if(mentee.notRegistered())
                full_name = mentee.getUcid();
            String ucid = mentor.getUcid();
            messenger.setText(full_name);
            if(!mentee.getAvi().equals("N/A"))
                Picasso.get().load(mentee.getAvi()).into(messenger_img);
            notifyLikesText = NotificationText.likes(ucid);
            notifyDislikesText = NotificationText.dislikes(ucid);
            getMessage(mentor.getUcid(), mentee.getUcid(), view);
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if(isStudent(USER_TYPE))
        {
            String full_name = mentor.getFname() + " " + mentor.getLname();
            messenger.setText(full_name);
            getMessage(mentee.getUcid(), mentor.getUcid(), view);
        }
        else {
            String full_name = mentee.getFname() + " " + mentee.getLname();
            if(mentee.notRegistered())
                full_name = mentee.getUcid();
            messenger.setText(full_name);
            getMessage(mentor.getUcid(), mentee.getUcid(), view);
        }
    }

    private boolean isStudent(SharedPreferences type)
    {
        return type.getString("type", null).equals("student");
    }

    private void getMessage(final String RECEIVER, final String SENDER, final View view)
    {
        RequestQueue queue = Volley.newRequestQueue(view.getContext());
        StringRequest stringRequest_1 = new StringRequest(Request.Method.POST, WebServer.getQueryLink(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("DEBUG_OUTPUT","Server Response: "+response);
                        String [] reply = response.split("\\|");
                        if (reply[0].equals("empty")) {
                            String msg = "No new feedback from " + SENDER;
                            String day = "Date: Not Available";
                            FEEDBACK.setText(msg);
                            date.setText(day);
                            setBar(FEEDBACK, bar);

                        } else {
                            FEEDBACK.setText(reply[0]);
                            setBar(FEEDBACK, bar);
                            String d = DateTimeFormat.formatDate(reply[1]);
                            String day = "Date: " + d;
                            date.setText(day);
                            setLiking(reply[2]);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("DEBUG_OUTPUT","Volley Error: "+error);
                error.printStackTrace();
                if(error instanceof TimeoutError)
                    Toast.makeText(
                            view.getContext(),
                            "Request timed out. Check your network settings.",
                            Toast.LENGTH_SHORT
                    ).show();
                else if(error instanceof NetworkError)
                    Toast.makeText(
                            view.getContext(),
                            "Can't connect to the internet",
                            Toast.LENGTH_SHORT
                    ).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("action", "getFeedback");
                params.put("receiver", RECEIVER);
                params.put("sender", SENDER);  //Change this later <------
                return params;
            }
        };

        queue.add(stringRequest_1);
        stringRequest_1.setRetryPolicy(new DefaultRetryPolicy(
                3000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        );
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId()){

            case R.id.m_fab:
                startActivity(new Intent(getActivity(), EditMessage.class));
                break;

            case R.id.thumbsup:
                if(up){
                    up = false;
                    thumbs_up.setImageResource(R.drawable.ic_thumb_up);
                    likeOrDislike(mentee.getUcid(), mentor.getUcid(), view, "0", FEEDBACK);
                }
                else{
                    up = true;
                    down = false;
                    thumbs_down.setImageResource(R.drawable.ic_thumb_down);
                    thumbs_up.setImageResource(R.drawable.ic_thumb_up_green);
                    postToast("up");
                    likeOrDislike(mentee.getUcid(), mentor.getUcid(), view, "1", FEEDBACK);
                    PushMessageToFCM.send(getContext(), notifyLikesText[0], notifyLikesText[1]);
                }
                break;

            case R.id.thumbsdown:
                if(down){
                    down = false;
                    thumbs_down.setImageResource(R.drawable.ic_thumb_down);
                    likeOrDislike(mentee.getUcid(), mentor.getUcid(), view, "0", FEEDBACK);
                }
                else{
                    down = true;
                    up = false;
                    thumbs_up.setImageResource(R.drawable.ic_thumb_up);
                    thumbs_down.setImageResource(R.drawable.ic_thumb_down_red);
                    postToast("down");
                    likeOrDislike(mentee.getUcid(), mentor.getUcid(), view, "2", FEEDBACK);
                    PushMessageToFCM.send(getContext(), notifyDislikesText[0], notifyDislikesText[1]);
                }
                break;

            case R.id.contact_user:
                startActivity(new Intent(getActivity(), SendEmail.class));
                break;

            case R.id.acc_info:
                if(isStudent(USER_TYPE))
                    startActivity(new Intent(getActivity(), MentorActivity.class));
                else
                    startActivity(new Intent(getActivity(), StudentActivity.class));
                break;

            case R.id.help:
                Fragment help = new FAQFragment();
                if(getFragmentManager() != null)
                {
                    FragmentTransaction transaction2 = getFragmentManager().beginTransaction();
                    transaction2.replace(R.id.fragment_container, help);
                    transaction2.addToBackStack(null);
                    // Commit the transaction
                    transaction2.commit();
                    SideBar.navigationView.setCheckedItem(R.id.faq);
                }
                break;

            case R.id.report:
                Intent intent = new Intent(getActivity(), ReportActivity.class);
                String msg = FEEDBACK.getText().toString();
                String name = messenger.getText().toString();
                intent.putExtra("com.example.mentorapp.Report.activity", msg);
                intent.putExtra("com.example.mentorapp.Report.name", name);
                startActivity(intent);
                break;

            case R.id.share:
                String message = FEEDBACK.getText().toString();
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("text/plain");
                share.putExtra(Intent.EXTRA_TEXT, message);
                startActivity(Intent.createChooser(share, "Choose Social Media Platform"));
                break;

            default:
                break;
        }
    }

    private void postToast(String type)
    {
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

    private void likeOrDislike(final String STUDENT, final String MENTOR, final View view,
                               final String status, final TextView msg)
    {
        RequestQueue queue = Volley.newRequestQueue(view.getContext());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, WebServer.getQueryLink(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("DEBUG_OUTPUT","Server Response: "+response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("DEBUG_OUTPUT","Volley Error: "+error);
                error.printStackTrace();
                if(error instanceof TimeoutError)
                    Toast.makeText(
                            view.getContext(),
                            "Request timed out. Check your network settings.",
                            Toast.LENGTH_SHORT
                    ).show();
                else if(error instanceof NetworkError)
                    Toast.makeText(
                            view.getContext(),
                            "Can't connect to the internet",
                            Toast.LENGTH_SHORT
                    ).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("action", "liking");
                params.put("student", STUDENT);
                params.put("mentor", MENTOR);
                params.put("msg", msg.getText().toString());
                params.put("status", status);
                return params;
            }
        };

        queue.add(stringRequest);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                3000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        );
    }

    private void setLiking(String status)
    {
        switch (status)
        {
            case "0":
                this.thumbs_up.setImageResource(R.drawable.ic_thumb_up);
                this.thumbs_down.setImageResource(R.drawable.ic_thumb_down);
                this.up = false;
                this.down = false;
                break;

            case "1":
                this.thumbs_up.setImageResource(R.drawable.ic_thumb_up_green);
                this.thumbs_down.setImageResource(R.drawable.ic_thumb_down);
                this.up = true;
                this.down = false;
                break;

            case "2":
                this.thumbs_up.setImageResource(R.drawable.ic_thumb_up);
                this.thumbs_down.setImageResource(R.drawable.ic_thumb_down_red);
                this.up = false;
                this.down = true;

            default:
                break;
        }
    }

    private void setBar(TextView feedback, ConstraintLayout layout)
    {
        String feed = feedback.getText().toString();
        if(feed.contains("No new feedback from "))
            layout.setVisibility(View.GONE);
        else
            layout.setVisibility(View.VISIBLE);
    }
}

