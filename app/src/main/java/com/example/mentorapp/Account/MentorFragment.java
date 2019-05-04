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
import android.widget.EditText;
import android.widget.ImageView;
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
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class MentorFragment extends Fragment {

    SharedPreferences MENTOR, USER_TYPE;
    SharedPreferences.Editor editor;
    String url = "https://web.njit.edu/~kas58/mentorDemo/query.php";
    View view;
    ImageView AVI;
    EditText MTR_NAME, MTR_EMAIL, MTR_UCID, MTR_DATE, MTR_DEGREE, MTR_OCC, MTR_MENTEE;
    TextView EDIT, DONE;
    EditText [] list;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.account_mentor, container, false);
        MENTOR = getActivity().getSharedPreferences("MENTOR", Context.MODE_PRIVATE);
        USER_TYPE = getActivity().getSharedPreferences("USER_TYPE", Context.MODE_PRIVATE);

        AVI = view.findViewById(R.id.picasso);
        MTR_NAME = view.findViewById(R.id.acc_mtr_name);
        MTR_EMAIL = view.findViewById(R.id.acc_mtr_email);
        MTR_UCID = view.findViewById(R.id.mtr_ucid1);
        MTR_DATE = view.findViewById(R.id.mtr_date1);
        MTR_DEGREE = view.findViewById(R.id.mtr_degree1);
        MTR_OCC = view.findViewById(R.id.mtr_occ1);
        MTR_MENTEE = view.findViewById(R.id.mtr_mentee1);
        EDIT = view.findViewById(R.id.m_edit);
        DONE = view.findViewById(R.id.m_done);


        Picasso.get().load(MENTOR.getString("avi", null)).into(AVI);
        MTR_NAME.setText(MENTOR.getString("fname", null) + " " + MENTOR.getString(
                "lname", null));
        MTR_EMAIL.setText(MENTOR.getString("email", null));
        MTR_UCID.setText(MENTOR.getString("ucid", null));
        MTR_DATE.setText(MENTOR.getString("grad_date", null));
        MTR_DEGREE.setText(MENTOR.getString("degree", null));
        MTR_OCC.setText(MENTOR.getString("occupation", null));
        MTR_MENTEE.setText(MENTOR.getString("mentee", null));

        list = new EditText [] {MTR_NAME, MTR_EMAIL, MTR_DATE, MTR_DEGREE, MTR_OCC};
        return view;
    }

    @Override
    public void onStart()
    {
        super.onStart();
        if(isMentor(USER_TYPE))
        {
            EDIT.setVisibility(View.VISIBLE);
            DONE.setVisibility(View.INVISIBLE);

            EDIT.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DONE.setVisibility(View.VISIBLE);
                    EDIT.setVisibility(View.INVISIBLE);
                    editText(list, true);
                }
            });

            DONE.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DONE.setVisibility(View.INVISIBLE);
                    EDIT.setVisibility(View.VISIBLE);
                    editText(list, false);
                    updateSharedPrefs(editor, list);
                    updateChanges(view, MENTOR, url, "updateMentorRecord");
                    postToast();
                }
            });
        }
        else{
            EDIT.setVisibility(View.INVISIBLE);
            DONE.setVisibility(View.INVISIBLE);
        }
    }

    private void editText(EditText [] texts, boolean edit)
    {

        if(edit)
        {
            for(int i = 0; i < texts.length; i++){
                texts[i].setEnabled(true);
            }
        }
        else{
            for(int i = 0; i < texts.length; i++){
                texts[i].setEnabled(false);
            }
        }
    }

    private boolean isMentor(SharedPreferences type)
    {
        if(type.getString("type", null).equals("mentor"))
        {
            return true;
        }
        else{
            return false;
        }
    }

    private void updateSharedPrefs(SharedPreferences.Editor e, EditText [] texts)
    {
        e = MENTOR.edit();
        String [] name = texts[0].getText().toString().split(" ");
        e.putString("fname", name[0]);
        e.putString("lname", name[1]);
        e.putString("email", texts[1].getText().toString());
        e.putString("grad_date", texts[2].getText().toString());
        e.putString("occupation", texts[3].getText().toString());
        e.putString("degree", texts[4].getText().toString());
        e.apply();
        SideBar bar = (SideBar) getActivity();
        bar.user_name.setText(name[0] + " " + name[1]);
        bar.user_email.setText(texts[1].getText().toString());
    }

    private void updateChanges(View v, final SharedPreferences list, String url, final String action)
    {
        RequestQueue queue = Volley.newRequestQueue(v.getContext());
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
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
                params.put("action", action);
                params.put("mentor", list.getString("ucid", null));
                params.put("fname", list.getString("fname", null));
                params.put("lname", list.getString("lname", null));
                params.put("email", list.getString("email", null));
                params.put("grad_date", list.getString("grad_date", null));
                params.put("occupation", list.getString("occupation", null));
                params.put("degree", list.getString("degree", null));
                return params;
            }
        };
        queue.add(request);
    }

    private void postToast()
    {
        Context context = getContext();
        CharSequence text = "Updated Changes.";
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }
}