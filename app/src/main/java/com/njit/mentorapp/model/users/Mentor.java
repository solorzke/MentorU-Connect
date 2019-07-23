package com.njit.mentorapp.model.users;

import android.content.Context;
import android.content.SharedPreferences;

public class Mentor
{
    private String ucid, fname, lname, email, degree, age, birthday, grad_date, occupation, mentee, avi;
    private SharedPreferences USER;

    public Mentor(Context context)
    {
        this.USER = context.getSharedPreferences("MENTOR", Context.MODE_PRIVATE);
        this.ucid = USER.getString("ucid", null);
        this.fname = USER.getString("fname", null);
        this.lname = USER.getString("lname", null);
        this.email = USER.getString("email", null);
        this.degree = USER.getString("degree", null);
        this.age = USER.getString("age", null);
        this.birthday = USER.getString("birthday", null);
        this.occupation = USER.getString("occupation", null);
        this.mentee = USER.getString("mentee", null);
        this.grad_date = USER.getString("grad_date", null);
        this.avi = USER.getString("avi", null);
    }

    public String getUcid() {
        return ucid;
    }

    public void setUcid(String ucid) {
        this.ucid = ucid;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDegree() {
        return degree;
    }

    public void setDegree(String degree) {
        this.degree = degree;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getGrad_date() {
        return grad_date;
    }

    public void setGrad_date(String grad_date) {
        this.grad_date = grad_date;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public String getMentee() {
        return mentee;
    }

    public void setMentee(String mentee) {
        this.mentee = mentee;
    }

    public String getAvi() {
        return avi;
    }

    public void setAvi(String avi) {
        this.avi = avi;
    }
}
