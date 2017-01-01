package com.oddhov.facebookcalendarsync.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

class EventUtils {

    static Long convertDateToEpochFormat(String dateAndTime) {
        //2023-10-28T20:00:00+0200
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ"); // TODO
        Date date = null;
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
}
