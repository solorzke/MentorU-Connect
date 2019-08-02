package com.njit.mentorapp.model.users;

import android.content.Context;
import android.content.SharedPreferences;

public class ReceivingUser
{
    private SharedPreferences channel;

    public ReceivingUser(Context context, String receiving)
    {
        channel = context.getSharedPreferences("CHANNEL", Context.MODE_PRIVATE);
        clear();
        channel.edit().putString("ucid", receiving).apply();
    }

    public ReceivingUser(Context context)
    {
        channel = context.getSharedPreferences("CHANNEL", Context.MODE_PRIVATE);
    }

    public String getReceivingUser()
    {
        return channel.getString("ucid", null);
    }

    public void setReceivingUser(String receiving)
    {
        channel.edit().putString("ucid", receiving).apply();
    }

    public void clear()
    {
        channel.edit().clear().apply();
    }
}
