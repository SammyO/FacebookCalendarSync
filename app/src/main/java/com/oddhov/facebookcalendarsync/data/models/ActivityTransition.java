package com.oddhov.facebookcalendarsync.data.models;

import com.oddhov.facebookcalendarsync.R;

public class ActivityTransition {
    public static final ActivityTransition NEXT = new ActivityTransition(
            R.anim.slide_in_right, R.anim.slide_out_left);
    public static final ActivityTransition BACK = new ActivityTransition(
            R.anim.slide_in_left, R.anim.slide_out_right);

    private int mEnter;
    private int mExit;

    public ActivityTransition(int enter, int exit) {
        this.mEnter = enter;
        this.mExit = exit;
    }

    public int getEnter() {
        return mEnter;
    }

    public int getExit() {
        return mExit;
    }
}