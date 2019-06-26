package com.example.mentorapp.CoachingLog;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.mentorapp.R;
import java.util.HashMap;
import java.util.Map;

public class MeetingStatus extends Fragment {

    String status, type;
    View view;
    String url = "https://web.njit.edu/~kas58/mentorDemo/Model/index.php";
    SharedPreferences SESSION, OTHER_USER;
    ImageView state;
    ProgressBar pending;
    TextView t1, t2;

    /* When the fragment view is created, initialize necessary objects */

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_request_status, container, false);
        SESSION = getActivity().getSharedPreferences("USER", Context.MODE_PRIVATE);
        OTHER_USER = getActivity().getSharedPreferences("MENTOR", Context.MODE_PRIVATE);
        t1 = view.findViewById(R.id.status_title);
        t2 = view.findViewById(R.id.status_title2);
        state = view.findViewById(R.id.status);
        pending = view.findViewById(R.id.pending);
        return view;

    }

    /* After fragment is created, begin running the view.
    * Send a request to learn the status of the meeting request, and reflect that status contextually
    * using appropriate images, text on the view*/
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        receiveStatus(view, url, "checkStatus", SESSION.getString("ucid", null),
                OTHER_USER.getString("ucid", null));
    }

    /* When you come back into focus of the fragment, resend a status check request. */
    @Override
    public void onResume() {
        super.onResume();
        receiveStatus(view, url, "checkStatus", SESSION.getString("ucid", null),
                OTHER_USER.getString("ucid", null));
    }

    /* Retrieves status info about the meeting request */
    private void receiveStatus(View v, String url, final String action, final String currentUser,
                               final String otherUser) {
        RequestQueue queue = Volley.newRequestQueue(v.getContext());
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println(response);
                if (response.equals("empty")) {
                    status = "0";
                } else {
                    System.out.println(response);
                    String[] res = response.split("\\|");
                    /* Use the status info + checkStatus() to appropriately reflect what the status is
                     * to the view */
                    checkStatus(state, pending, res[1], res[0], t1, t2);
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
                params.put("action", action);
                params.put("currentUser", currentUser);
                params.put("otherUser", otherUser);
                return params;
            }
        };
        queue.add(request);
    }

    /* Apply contextual assets/info that you'll display to the view based on the request status */
    private void checkStatus(ImageView state, ProgressBar pending, String status, String type,
                             TextView t1, TextView t2) {
        String s = status;
        switch (s) {
            case "1":
                state.setVisibility(View.VISIBLE);
                state.setImageResource(R.drawable.approved);
                pending.setVisibility(View.INVISIBLE);
                if (type.equals("sender")) {
                    t1.setText("Your meeting request was accepted.");
                    t2.setText("View the meeting info.");
                } else if (type.equals("receiver")) {
                    t1.setText("You've accepted the meeting request.");
                    t2.setText("View the details on the next page.");
                }
                break;
            case "2":
                state.setVisibility(View.VISIBLE);
                state.setImageResource(R.drawable.denied);
                pending.setVisibility(View.INVISIBLE);
                if (type.equals("sender")) {
                    t1.setText("The meeting request was canceled.");
                    t2.setText("Schedule a new meeting.");
                } else if (type.equals("receiver")) {
                    t1.setText("You canceled the meeting request.");
                    t2.setText("Schedule a new one!");
                }
                break;
            case "3":
                if (type.equals("sender")) {
                    state.setVisibility(View.INVISIBLE);
                    pending.setVisibility(View.VISIBLE);
                    t1.setText("Your request is waiting to be approved!");
                    t2.setText("Check back later.");
                } else if (type.equals("receiver")) {
                    state.setVisibility(View.VISIBLE);
                    state.setImageResource(R.drawable.notification);
                    pending.setVisibility(View.INVISIBLE);
                    t1.setText("You've received a meeting request!");
                    t2.setText("Check the details.");
                }
                break;
            default:
                state.setVisibility(View.VISIBLE);
                state.setImageResource(R.drawable.question);
                pending.setVisibility(View.INVISIBLE);
                t1.setText("You haven't received/sent a meeting request yet!");
                t2.setText("Schedule one!");
                break;
        }
    }
}
