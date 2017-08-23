package com.oddhov.facebookcalendarsync.data.models;

import android.text.TextUtils;

public enum CustomTime {
    HALF_HOUR(0.5, "30 minutes"),
    ONE_HOUR(1, "1 hour"),
    TWO_HOURS(2, "2 hours"),
    SIX_HOURS(6, "6 hours"),
    TWELVE_HOURS(12, "12 hours"),
    TWENTY_FOUR_HOURS(24, "24 hours");

    private final double mValue;
    private final String mDisplayString;

    CustomTime(double value, String displayString) {
        this.mValue = value;
        this.mDisplayString = displayString;
    }

    public int getTimeInMinutes() {
        return (int) (mValue * 3600); //TODO make sure this works for 0.5 hour
    }

    public String getTimeInMinutesString() {
        return String.valueOf((long) (mValue * 3600));
    }

    public String getTimeInMinutesDisplayString() {
        return mDisplayString;
    }

    public static CustomTime getEnumValueFromTimeInMinutesString(String timeInMinutes) {
        for (CustomTime customTime : CustomTime.values()) {
            if (TextUtils.equals(timeInMinutes, customTime.getTimeInMinutesString())) {
                return customTime;
            }
        }
        return null;
    }
}

