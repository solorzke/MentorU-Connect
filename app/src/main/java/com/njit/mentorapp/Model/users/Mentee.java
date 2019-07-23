package com.njit.mentorapp.Model.users;

import android.content.Context;
import android.content.SharedPreferences;

public class Mentee
{
    private String ucid, fname, lname, email, degree, age, birthday, grade, grad_date;
    private SharedPreferences USER;

    public Mentee(Context context)
    {
        this.USER = context.getSharedPreferences("STUDENT", Context.MODE_PRIVATE);
        this.ucid = USER.getString("ucid", null);
        this.fname = USER.getString("fname", null);
        this.lname = USER.getString("lname", null);
        this.email = USER.getString("email", null);
        this.degree = USER.getString("degree", null);
        this.age = USER.getString("age", null);
        this.birthday = USER.getString("birthday", null);
        this.grade = USER.getString("grade", null);
        this.grad_date = USER.getString("grad_date", null);
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

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getGrad_date() {
        return grad_date;
    }

    public void setGrad_date(String grad_date) {
        this.grad_date = grad_date;
    }

    public SharedPreferences getUSER() {
        return USER;
    }

    public void setUSER(SharedPreferences USER) {
        this.USER = USER;
    }
}