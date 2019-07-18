package com.njit.mentorapp.model.Service;

import android.support.annotation.NonNull;
import android.util.Log;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

public class FireBaseServer
{
    final private static String SERVER_KEY = "AAAADKj9TzM:APA91bFuGihxV1RnLY0t1W8TB6D49df08jKadQHroe1QbLz4X_mYauFJEmB41hwmcBXdHOw-boP7FIdxvF1cJNZuGYnqLFhyMzik1MAUzWfWqp03aRRDvhAQ3SR5ZKyTMr-4EDK21o4F";

    final private static String FCM_API = "https://fcm.googleapis.com/fcm/send";

    public static String getFCM_API() { return FCM_API; }

    public static String getSERVER_KEY() { return SERVER_KEY; }

    public static void subscribeToTopic(String TOPIC)
    {
        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC)
                .addOnCompleteListener(new OnCompleteListener<Void>()
                {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "Users Subscribed!";
                        if (!task.isSuccessful()) {
                            msg = "Message Delivered";
                        }
                        Log.d("OUTPUT", msg);
                    }
                });
    }
}
