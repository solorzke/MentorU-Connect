package com.njit.mentorapp.model.service;

import android.content.Context;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class PushMessageToFCM
{
    /* This is the process of how to send notifications to another user using FireBase Server.
     * Requirements:
     *    > both users (mentee/mentor) must be subscribed to a topic
     *    > a unique topic_id
     *    > a title string, a message string
     *    > the server api key (only as passing reference aka var)
     *    > a working class capable of receiving notifications from the server (FireBaseInstanceService)
     *
     * Process:
     *    > Get the TOPIC_ID to filter who'll get this message (Only two people are allowed to
     *          exchange notifs per topic. In order to erase the topic, both users must unsubscribe.)
     *    > Have ready a title and message text for your notification to fill in our JSON objects.
     *    > Use the 'send' function and fill in the required parameters which include:
     *          > title, body, and APPLICATION_CONTEXT
     *    > Message is sent to the FCM Server, and sends it to the corresponding users that are
     *          subscribed to the topic. Look into FireBaseInstanceService class to edit
     *          whether to accept this notification or not.
     *    > Notification is created from the FCM server using the title and message text and is fired back to the target
     *    > Target device receives the notification.
     */
    private static JSONObject notification, notifcationBody;

    private static void setJsonObjects(String NOTIFICATION_TITLE, String NOTIFICATION_MESSAGE)
    {
        String TOPIC = "/topics/kas58ppp43"; //topic must match with what the receiver subscribed to
        notification = new JSONObject();
        notifcationBody = new JSONObject();
        try
        {
            notifcationBody.put("title", NOTIFICATION_TITLE);
            notifcationBody.put("message", NOTIFICATION_MESSAGE);

            notification.put("to", TOPIC);
            notification.put("data", notifcationBody);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private static void sendNotification(final Context context)
    {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                FireBaseServer.getFCM_API(), notification,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.print(response);
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, "Request error", Toast.LENGTH_LONG).show();
                        System.out.print("onErrorResponse: Didn't work");
                    }
                }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError
            {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", "key=" + FireBaseServer.getSERVER_KEY());
                params.put("Content-Type", "application/json");
                return params;
            }
        };
        MySingleton.getInstance(context).addToRequestQueue(jsonObjectRequest);
    }

    public static void send(final Context context, String NOTIFICATION_TITLE, String NOTIFICATION_BODY)
    {
        setJsonObjects(NOTIFICATION_TITLE, NOTIFICATION_BODY);
        sendNotification(context);
    }

}
