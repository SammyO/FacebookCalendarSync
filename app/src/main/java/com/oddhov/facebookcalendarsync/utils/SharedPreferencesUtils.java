package com.oddhov.facebookcalendarsync.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesUtils {
    private Context mContext;
    private static final String SHARED_PREFERENCES_FILE = "com.oddhov.facebookcalendarsync.shared_preferences";
    private static final String SHARED_PREFERENCES_SYNC_MODE_ONLY_UPCOMING = "com.oddhov.facebookcalendarsync.shared_preferences.sync_mode_only_upcoming";

    public SharedPreferencesUtils(Context context) {
        this.mContext = context;
    }

    public void setSyncOnlyUpcoming(boolean value) {
        SharedPreferences prefs = mContext.getSharedPreferences(SHARED_PREFERENCES_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(SHARED_PREFERENCES_SYNC_MODE_ONLY_UPCOMING, value);
        editor.apply();
    }

    public boolean getSyncOnlyUpcoming() {
        SharedPreferences prefs = mContext.getSharedPreferences(SHARED_PREFERENCES_FILE, Context.MODE_PRIVATE);
        return prefs.getBoolean(SHARED_PREFERENCES_SYNC_MODE_ONLY_UPCOMING, false);
    }
}
