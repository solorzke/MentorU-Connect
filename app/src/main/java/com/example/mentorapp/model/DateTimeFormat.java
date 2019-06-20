package com.example.mentorapp.model;

public class DateTimeFormat {

    public static String formatDate(String date)
    {
        /* Y-M-D -> M/D/YYYY Ex: 2019-05-27 -> 05/27/2019 */
        String [] d = date.split("-"); //Should be 3 indices
        String new_date = d[1] + "/" + d[2] + "/" + d[0];
        return new_date;
    }

    public static String formatDateSQL(String date)
    {
        /* MM/DD/YYYY -> Y-M-D Ex: 09/15/1995 -> 1995-09-15 */
        String [] d = date.split("/");
        String new_date = d[2] + "-" + d[0] + "-" + d[1];
        return new_date;
    }
}
