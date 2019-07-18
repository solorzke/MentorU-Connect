package com.njit.mentorapp.model.Service;

public class FireBaseServer
{
    final private static String SERVER_KEY = "AAAADKj9TzM:APA91bFuGihxV1RnLY0t1W8TB6D49df08jKadQHroe1QbLz4X_mYauFJEmB41hwmcBXdHOw-boP7FIdxvF1cJNZuGYnqLFhyMzik1MAUzWfWqp03aRRDvhAQ3SR5ZKyTMr-4EDK21o4F";

    final private static String FCM_API = "https://fcm.googleapis.com/fcm/send";

    public static String getFCM_API() { return FCM_API; }

    public static String getSERVER_KEY() { return SERVER_KEY; }
}
