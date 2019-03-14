package com.example.mentorapp.model;

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
}