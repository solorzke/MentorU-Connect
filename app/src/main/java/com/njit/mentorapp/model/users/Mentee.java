package com.njit.mentorapp.model.users;

import android.content.Context;
import android.content.SharedPreferences;

public class Mentee
{
    private SharedPreferences user;

    public Mentee(Context context)
    {
        this.user = context.getSharedPreferences("STUDENT", Context.MODE_PRIVATE);
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

    public String getGrade() {
        return user.getString("grade", null);
    }

    public void setGrade(String grade) {
        user.edit().putString("grade", grade).apply();
    }

    public String getGrad_date() {
        return user.getString("grad_date", null);
    }

    public void setGrad_date(String grad_date) {
        user.edit().putString("grad_date", grad_date).apply();
    }

    public SharedPreferences getUSER() {
        return user;
    }

    public void setUSER(SharedPreferences user) {
        this.user = user;
    }

    public String getFullName(){
        return getFname() + " " + getLname();
    }
}