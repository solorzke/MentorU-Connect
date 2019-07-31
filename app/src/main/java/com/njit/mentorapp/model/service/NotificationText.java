package com.njit.mentorapp.model.service;

public class NotificationText
{
    public static String [] goal(String ucid)
    {
        return new String [] {"Goal Complete", ucid + " has completed a goal!"};
    }

    public static String [] message(String ucid)
    {
        return new String [] {"New Message", "Message from " + ucid + "!"};
    }

    public static String [] goalChange(String ucid)
    {
        return new String [] {"Goal Update", ucid + " added new goal changes!"};
    }

    public static String [] likes(String ucid)
    {
        return new String [] {"Message Update", ucid + " liked your message!"};
    }

    public static String [] dislikes(String ucid)
    {
        return new String [] {"Message Update", ucid + " dislikes your message!"};
    }

    public static String [] requestMeeting(String ucid)
    {
        return new String [] {"Meeting Request", ucid + " has requested a meeting!"};
    }

    public static String [] meetingResponse(String ucid, String response)
    {
        return new String [] {"Meeting " + response, ucid + " " + response + " your meeting request"};
    }
}
