package com.njit.mentorapp.Model.Service;

import android.support.annotation.NonNull;
import android.util.Log;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

public class FireBaseServer
{
    final private static String SERVER_KEY = "AAAADKj9TzM:APA91bFuGihxV1RnLY0t1W8TB6D49df08jKadQHroe1QbLz4X_mYauFJEmB41hwmcBXdHOw-boP7FIdxvF1cJNZuGYnqLFhyMzik1MAUzWfWqp03aRRDvhAQ3SR5ZKyTMr-4EDK21o4F";

    final private static String FCM_API = "https://fcm.googleapis.com/fcm/send";

    private static String TOPIC_ID = null;

    private static boolean IN_THE_DB = false;

    public static String getFCM_API() { return FCM_API; }

    public static String getSERVER_KEY() { return SERVER_KEY; }

    /* Check if the user signing in belongs to the topic registered with the device. */
    public static boolean topicValidation(String USER)
    {
        String id = getTopicID(USER);
        if(!id.equals("null"))
            return true;
        return false;
    }

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
    public static void registerToDB(String ucid_1, String ucid_2)
    {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("Communication");
        String key = db.push().getKey();
        db.child(key).child("user_1").setValue(ucid_1);
        db.child(key).child("user_2").setValue(ucid_2);
        db.child(key).child("topic_id").setValue(ucid_1+ucid_2);
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

    /* Return the topic id between two users */
    public static String getTopicID(final String UCID)
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
                            TOPIC_ID = data.child("topic_id").getValue().toString();
                            break loop;
                        }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("DEBUG_OUTPUT", databaseError.getMessage());
            }
        });

        if(TOPIC_ID != null)
            return TOPIC_ID;
        else
            return "null";
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
}
