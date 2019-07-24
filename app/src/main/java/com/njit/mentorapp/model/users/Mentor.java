package com.njit.mentorapp.model.users;

import android.content.Context;
import android.content.SharedPreferences;

public class Mentor
{
    private SharedPreferences user;

    public Mentor(Context context){
        this.user = context.getSharedPreferences("MENTOR", Context.MODE_PRIVATE);
    }

    public String getUcid() {
        return user.getString("ucid", null);
    }

    public void setUcid(String ucid) {
        user.edit().putString("ucid", ucid).apply();
    }

    public String getFname() {
        return user.getString("fname", null);
    }

    public void setFname(String fname) {
        user.edit().putString("fname", fname).apply();
    }

    public String getLname() {
        return user.getString("lname", null);
    }

    public void setLname(String lname) {
        user.edit().putString("lname", lname).apply();
    }

    public String getEmail() {
        return user.getString("email", null);
    }

    public void setEmail(String email) {
        user.edit().putString("email", email).apply();
    }

    public String getDegree() {
        return user.getString("degree", null);
    }

    public void setDegree(String degree) {
        user.edit().putString("degree", degree).apply();
    }

    public String getAge() {
        return user.getString("age", null);
    }

    public void setAge(String age) {
        user.edit().putString("age", age).apply();
    }

    public String getBirthday() {
        return user.getString("birthday", null);
    }

    public void setBirthday(String birthday) {
        user.edit().putString("birthday", birthday).apply();
    }

    public String getGrad_date() {
        return user.getString("grad_date", null);
    }

    public void setGrad_date(String grad_date) {
        user.edit().putString("grad_date", grad_date).apply();
    }

    public String getOccupation() {
        return user.getString("occupation", null);
    }

    public void setOccupation(String occupation) {
        user.edit().putString("occupation", occupation).apply();
    }

    public String getMentee() {
        return user.getString("mentee", null);
    }

    public void setMentee(String mentee) {
        user.edit().putString("mentee", mentee).apply();
    }

    public String getAvi() {
        return user.getString("avi", null);
    }

    public void setAvi(String avi) {
        user.edit().putString("avi", avi).apply();
    }

    public String getFullName(){
        return getFname() + " " + getLname();
    }
}
