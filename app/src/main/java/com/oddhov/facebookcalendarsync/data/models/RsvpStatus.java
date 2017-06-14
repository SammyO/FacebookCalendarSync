package com.oddhov.facebookcalendarsync.data.models;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

public enum RsvpStatus {
    @SerializedName("attending")
    ATTENDING("attending", "Attending"),

    @SerializedName(value = "maybe", alternate = {"unsure", "no_reply"})
    MAYBE("maybe", "Maybe"),

    @SerializedName("not_replied")
    NOT_REPLIED("not_replied", "No reply"),

    @SerializedName("declined")
    DECLINED("declined", "Declined");

    private String mFacebookParameter;
    private String mDisplayString;

    RsvpStatus(String facebookParameter, String displayString) {
        this.mFacebookParameter = facebookParameter;
        this.mDisplayString = displayString;
    }

    public String getFacebookParameter() {
        return mFacebookParameter;
    }

    public String getDisplayString() {
        return mDisplayString;
    }

    public static RsvpStatus getEnumFromFacebookParameter(String value) {
        for (RsvpStatus rsvpStatus : RsvpStatus.values()) {
            if (TextUtils.equals(value, rsvpStatus.getFacebookParameter())) {
                return rsvpStatus;
            }
        }
        return null;
    }

    public static RsvpStatus getEnumFromDisplayString(String value) {
        for (RsvpStatus rsvpStatus : RsvpStatus.values()) {
            if (TextUtils.equals(value, rsvpStatus.getDisplayString())) {
                return rsvpStatus;
            }
        }
        return null;
    }
}
