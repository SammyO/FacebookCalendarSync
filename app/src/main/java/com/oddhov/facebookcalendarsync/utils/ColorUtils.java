package com.oddhov.facebookcalendarsync.utils;

import com.oddhov.facebookcalendarsync.data.models.CalendarColour;

public class ColorUtils {

    public String getHexValueForColor(CalendarColour color) {
        switch (color) {
            case GREEN:
                return "#00ff00";
            case ORANGE:
                return "#ffa500";
            case PURPLE:
                return "#8b008b";
            case BLUE:
                return "#0000ff";
            case RED:
            default:
                return "#ff0000";
        }
    }
}
