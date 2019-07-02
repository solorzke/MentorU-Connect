package com.example.mentorapp.model;

import java.util.Calendar;
import java.util.GregorianCalendar;

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

    public static int getFullWeeks(int month)
    {
        Calendar start = Calendar.getInstance();
        int year = start.get(Calendar.YEAR);
        start.set(year, start.get(Calendar.MONTH), start.get(Calendar.DAY_OF_MONTH));

        Calendar end = Calendar.getInstance();
        end.set(year, month, 01);

        return getNrWeeksBetween(start, end);
    }

    private static int getNrWeeksBetween(Calendar start, Calendar end)
    {
        int weeks = 0;

        Calendar counter = new GregorianCalendar();
        counter.setTime( start.getTime() );
        counter.add(Calendar.DATE, 6);

        while( counter.before(end) )
        {
            if(counter.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) weeks++;
            counter.add(Calendar.DATE, 1);
        }

        return weeks;
    }

    public static String [] format12HourTime(int hour, int min)
    {
        int h = hour;
        String m;
        String timeset;

        if(hour > 12){  h -= 12; timeset = "PM";  }
        else if(hour == 0){  h += 12; timeset = "AM";  }
        else if(hour == 12){  timeset = "PM";  }
        else{  timeset = "AM";  }

        if(min < 10){ m = "0" + min; }
        else{  m = String.valueOf(min);  }

        String [] data = {Integer.toString(h), m, timeset};
        return data;
    }

    public static String format12HourTimeAsString(String str)
    {
        String [] date = str.split(":");
        int hour = Integer.parseInt(date[0]);
        int min = Integer.parseInt(date[1]);
        int h = hour;

        String m;
        String timeset;

        if(hour > 12){  h -= 12; timeset = "PM";  }
        else if(hour == 0){  h += 12; timeset = "AM";  }
        else if(hour == 12){  timeset = "PM";  }
        else{  timeset = "AM";  }

        if(min < 10){ m = "0" + min; }
        else{  m = String.valueOf(min);  }

        String [] data = {Integer.toString(h), m, timeset};
        return data[0] + ":" + data[1] + " " + timeset;

    }
}
