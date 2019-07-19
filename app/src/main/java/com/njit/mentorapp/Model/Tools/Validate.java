package com.njit.mentorapp.Model.Tools;

import android.content.SharedPreferences;
import android.widget.EditText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validate {

    private String username, password;

    public Validate(String username, String password){
        this.username = username;
        this.password = password;
    }

    public boolean isEmail(String user){

        if(!user.isEmpty()){
            if(!isDigitsOnly(user)){
                if(!hasSpace(user))
                    return true;
                else
                    return false;
            }
            else
                return false;
        }
        else
            return false;
    }

    public boolean isPassword(String pw){
        if(!pw.isEmpty()){
            if(pw.length() <= 20 && pw.length() > 2)
                return true;
            else
                return false;
        }
        else
            return false;
    }

    public boolean validation(){
        if(isEmail(this.username) && isPassword(this.password))
            return true;

        else
            return false;
    }

    public boolean isDigitsOnly(String str)
    {
        for (char c : str.toCharArray())
        {
            if (c < '0' || c > '9')
                return false;
        }

        return true;
    }

    public boolean hasSpace(String str){
        Pattern pattern = Pattern.compile("\\s");
        Matcher matcher = pattern.matcher(str);
        boolean found = matcher.find();
        return found;
    }

    /* Check if the string is blank or has nothing except whitespaces */
    public static boolean isBlank(String str)
    {
        /* If the character length is zero, it is blank. */
        if(str.length() < 1)
        {
            return true;
        }

        /* If it has a character or more, check if it only has whitespaces */
        else
        {
            for(int i = 0; i < str.length(); i++)
            {
                /* If the character isn't a whitespace, it isn't blank */
                if(str.charAt(i) != ' ')
                {
                    return false;
                }
            }
            /* At the end of the loop without finding a real character, then it is blank */
            return true;
        }
    }

    /* Validate for forms with EditText arrays */
    public static boolean checkForm(EditText[] events)
    {

        for(EditText event : events)
        {
            String data = event.getText().toString();

            /* If the character length is zero, it is blank. */
            if(data.length() < 1)
            {
                return false;
            }

            /* If it has a character or more, check if it only has whitespaces */
            else
            {
                boolean sentinel = false;
                for(int j = 0; j < data.length(); j++)
                {
                    /* If the character isn't a whitespace, it isn't blank */
                    if(data.charAt(j) != ' ')
                    {
                        sentinel = true;
                        break;
                    }
                    else if(j == data.length()-1)
                    {
                        sentinel = false;
                        break;
                    }
                }
                if(!sentinel)
                {
                    return false;
                }
            }
        }

        return true;
    }

    public static boolean isStudent(SharedPreferences type)
    {
        if (type.getString("type", null).equals("student"))
        {
            return true;
        }
        else {
            return false;
        }
    }
}