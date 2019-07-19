package com.njit.mentorapp.Model.Service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.njit.mentorapp.R;
import java.util.Map;
import java.util.Random;

public class FireBaseInstanceService extends FirebaseMessagingService
{
    // TODO(developer): Handle FCM messages here.
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage)
    {
        super.onMessageReceived(remoteMessage);
        if(remoteMessage.getData().isEmpty())
            showNotification(
                remoteMessage.getNotification().getTitle(),
                remoteMessage.getNotification().getBody()
            );
        else {
            showNotification(remoteMessage.getData());
        }
    }

    private void showNotification(Map<String, String> data)
    {
        String title = data.get("title").toString();
        String body = data.get("message").toString();

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String NOTIFICATION_CHANNEL_ID = "com.njit.mentorapp.model.test";

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            NotificationChannel channel = new NotificationChannel(
                    NOTIFICATION_CHANNEL_ID, "Notification",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription("Mentor U Connect");
            channel.enableLights(true);
            channel.setLightColor(Color.BLUE);
            channel.setVibrationPattern(new long [] {0, 1000, 500, 1000});
            channel.enableLights(true);
            manager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        builder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.mentor_u)
                .setContentTitle(title)
                .setContentText(body)
                .setContentInfo("Info");
        manager.notify(new Random().nextInt(), builder.build());
    }

    private void showNotification(String title, String body)
    {
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String NOTIFICATION_CHANNEL_ID = "com.njit.mentorapp.model.test";

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            NotificationChannel channel = new NotificationChannel(
                    NOTIFICATION_CHANNEL_ID, "Notification",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription("Mentor U Connect");
            channel.enableLights(true);
            channel.setLightColor(Color.BLUE);
            channel.setVibrationPattern(new long [] {0, 1000, 500, 1000});
            channel.enableLights(true);
            manager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        builder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.mentor_u)
                .setContentTitle(title)
                .setContentText(body)
                .setContentInfo("Info");
        manager.notify(new Random().nextInt(), builder.build());
    }

    @Override
    public void onNewToken(String s)
    {
        super.onNewToken(s);
    }
}
