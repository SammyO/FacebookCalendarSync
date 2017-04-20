package com.oddhov.facebookcalendarsync.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class EventUtils {

    static Long convertDateToEpochFormat(String dateAndTime) {
        //2023-10-28T20:00:00+0200
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ"); // TODO
        Date date;
        try {
            if (dateAndTime != null) {
                date = df.parse(dateAndTime);
                return date.getTime();
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    static String addOneHourToTimeStamp(String dateAndTime) throws ParseException {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ"); // TODO
        Date date;

        if (dateAndTime != null) {
            date = df.parse(dateAndTime);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.HOUR, 1);
            return df.format(calendar.getTime());
        } else {
            throw new ParseException("No value provided", 0); // TODO
        }

    }
}
