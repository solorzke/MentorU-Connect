package com.njit.mentorapp.model.users;

import android.content.Context;

public class UserType
{
    private String currentType;
    private String pairedType;
    private Context context;

    public UserType(Context context)
    {
        this.context = context;
        String type = context
                .getSharedPreferences("USER_TYPE", Context.MODE_PRIVATE)
                .getString("type", null);

        if(type.equals("student"))
        {
            this.currentType = "mentee";
            this.pairedType = "mentor";
        }
        else
        {
            this.currentType = "mentor";
            this.pairedType = "mentee";
        }
    }

    public String getCurrentUser()
    {
        if(currentType.equals("mentee"))
            return new Mentee(context).getUcid();
        else
            return new Mentor(context).getUcid();
    }

    public String getPairedUser()
    {
        if(currentType.equals("mentee"))
            return new Mentor(context).getUcid();
        else
            return new Mentee(context).getUcid();
    }

    public String getCurrentType()
    {
        return this.currentType;
    }

    public String getPairedType()
    {
        return this.pairedType;
    }

    public void clearSharedPrefs(){
        context.getSharedPreferences("USER_TYPE", Context.MODE_PRIVATE).edit().clear().apply();
    }
}
