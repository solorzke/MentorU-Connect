package com.njit.mentorapp.model.service;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.njit.mentorapp.model.tools.VolleyCallback;
import com.njit.mentorapp.model.users.ReceivingUser;

import java.util.HashMap;
import java.util.Map;

public class FireBaseServer
{
    final private static String SERVER_KEY = "AAAADKj9TzM:APA91bFuGihxV1RnLY0t1W8TB6D49df08jKadQHroe1QbLz4X_mYauFJEmB41hwmcBXdHOw-boP7FIdxvF1cJNZuGYnqLFhyMzik1MAUzWfWqp03aRRDvhAQ3SR5ZKyTMr-4EDK21o4F";

    final private static String FCM_API = "https://fcm.googleapis.com/fcm/send";

    private static boolean IN_THE_DB = false;

    public static String getFCM_API() { return FCM_API; }

    public static String getSERVER_KEY() { return SERVER_KEY; }

    /* Have the user subscribed to the topic id */
    public static void subscribeToTopic(String TOPIC_ID)
    {
        FirebaseMessaging
                .getInstance()
                .subscribeToTopic(TOPIC_ID)
                .addOnCompleteListener(new OnCompleteListener<Void>()
                {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "Users Subscribed!";
                        if (!task.isSuccessful()) {
                            msg = "Subscription didn't go through";
                        }
                        Log.d("DEBUG_OUTPUT", msg);
                    }
                });
    }

    //TODO (Developer) Write function to unsubscribe from topic to avoid getting notifications
    public static void unsubcribeToTopic(String TOPIC)
    {
        FirebaseMessaging
                .getInstance()
                .unsubscribeFromTopic(TOPIC)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "Unsubscribed";
                        if (!task.isSuccessful()) {
                            msg = "unSubscription didn't go through";
                        }
                        Log.d("DEBUG_OUTPUT", msg);
                    }
                });
    }

    /* Single-Use only per pairing */
    public static void registerToDB(String mentee, String mentor)
    {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("Communication");
        String key = db.push().getKey();
        db.child(key).child("mentee").setValue(mentee);
        db.child(key).child("mentor").setValue(mentor);
        db.child(key).child("topic_id").setValue(mentee+mentor);
    }

    /* Unregister pairing  */
    public static void unRegisterToDB(final String UCID)
    {
        final DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("Communication");
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                loop:
                for(DataSnapshot data : dataSnapshot.getChildren())

                    for(DataSnapshot user : data.getChildren())

                        if(user.getValue().equals(UCID))
                        {
                            Log.d("DEBUG_OUTPUT", data.getKey().toString());
                            db.child(data.getKey()).setValue(null);
                            break loop;
                        }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("DEBUG_OUTPUT", databaseError.getMessage());
            }
        });
    }

    /* Return the topic id between two users from 'FireBase' only */
    public static void getTopicID(final FireBaseCallback callback, final String UCID)
    {
        final DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("Communication");
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                loop:
                for(DataSnapshot data : dataSnapshot.getChildren())

                    for(DataSnapshot user : data.getChildren())
                        if(user.getValue().equals(UCID))
                        {
                            callback.onCallback(data.child("topic_id").getValue().toString());
                            break loop;
                        }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("DEBUG_OUTPUTt", databaseError.getMessage());
            }
        });
    }

    /* Verify if the user is registered in the FireBase database child 'Communication' */
    public static boolean inTheDB(final String UCID)
    {
        final DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("Communication");
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                loop:
                for(DataSnapshot data : dataSnapshot.getChildren())

                    for(DataSnapshot user : data.getChildren())
                        if(user.getValue().equals(UCID))
                        {
                            IN_THE_DB = true;
                            break loop;
                        }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("DEBUG_OUTPUT", databaseError.getMessage());
            }
        });

        return IN_THE_DB;
    }

    /* Return the Receiving User ucid/username that belongs to the current user */
    public static void findReceivingUser(final String pairedUser, final String type, final FireBaseCallback callback)
    {
        final DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("Communication");
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot data : dataSnapshot.getChildren()){
                    String value = data.child(type).getValue().toString();
                    if(value.equals(pairedUser)) {
                        callback.onCallback(value);
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("DEBUG_OUTPUT", databaseError.getMessage());
            }
        });
    }

    /* Methods below are for accessing the server's SQL database. Here is where managing the topics
     * and inserting new users occurs. This is the current way to set up notification channels
     * between two users. */

    /* Retrieve the topic id that belongs to the user pair from the server. */
    public static void getTopicID(final Context context, final String [] data, final VolleyCallback callback)
    {
        RequestQueue rq = Volley.newRequestQueue(context);
        StringRequest request = new StringRequest(Request.Method.POST, WebServer.getQueryLink(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                callback.onCallback(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("DEBUG_OUTPUT", error.toString());
                error.printStackTrace();
                if(error instanceof TimeoutError)
                    Toast.makeText(
                            context,
                            "Request timed out. Try Again.",
                            Toast.LENGTH_SHORT
                    ).show();
                else if(error instanceof NetworkError)
                    Toast.makeText(
                            context,
                            "Can't connect to the internet",
                            Toast.LENGTH_SHORT
                    ).show();

            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("action", "getTopicID");
                params.put("mentee", data[0]);
                params.put("mentor", data[1]);
                return params;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(
                3000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        );
        rq.add(request);
    }

    /* Add new user pairing to the DB, then create a unique topic_id identifier to use to sub-
     * scribe to FireBase to send/receive notification messages*/
    public static void setTopicID(final Context context, final String [] data, final VolleyCallback callback)
    {
        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest request = new StringRequest(Request.Method.POST, WebServer.getQueryLink(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                callback.onCallback(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("DEBUG_OUTPUT", error.toString());
                error.printStackTrace();
                if(error instanceof TimeoutError)
                    Toast.makeText(
                            context,
                            "Request timed out. Try Again.",
                            Toast.LENGTH_SHORT
                    ).show();
                else if(error instanceof NetworkError)
                    Toast.makeText(
                            context,
                            "Can't connect to the internet",
                            Toast.LENGTH_SHORT
                    ).show();

            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("action", "getTopicID");
                params.put("mentee", data[0]);
                params.put("mentor", data[1]);
                return params;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(
                3000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        );
        queue.add(request);

    }

    /* Return the receiving user username/ucid that is meant to receive the incoming notification
     * transmission */
    public static String getReceivingUser(Context context)
    {
        return new ReceivingUser(context).getReceivingUser();
    }
}
