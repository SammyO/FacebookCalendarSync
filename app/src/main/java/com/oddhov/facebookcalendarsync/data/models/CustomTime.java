package com.oddhov.facebookcalendarsync.data.models;

public enum CustomTime {
    HALF_HOUR(0.5),
    ONE_HOUR(1),
    TWO_HOURS(2),
    SIX_HOURS(6),
    TWELVE_HOURS(12),
    TWENTY_FOUR_HOURS(24);

    private final double mValue;

    CustomTime(double value) {
        mValue = value;
    }

    public long getTimeInMinutes() {
        return (long) (mValue * 3600); //TODO make sure this works for 0.5 hour
    }
}

