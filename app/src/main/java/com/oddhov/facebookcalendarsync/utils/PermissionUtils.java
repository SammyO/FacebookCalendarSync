package com.oddhov.facebookcalendarsync.utils;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

public class PermissionUtils {
    private Activity mActivity;

    public PermissionUtils(Activity context) {
        this.mActivity = context;
    }

    public boolean needsPermissions() {
        int readCalendarPermission = ContextCompat.checkSelfPermission(mActivity, Manifest.permission.READ_CALENDAR);
        int writeCalendarPermission = ContextCompat.checkSelfPermission(mActivity, Manifest.permission.WRITE_CALENDAR);
        int accountsPermission = ContextCompat.checkSelfPermission(mActivity, Manifest.permission.GET_ACCOUNTS);

        if (readCalendarPermission == PackageManager.PERMISSION_GRANTED &&
                writeCalendarPermission == PackageManager.PERMISSION_GRANTED &&
                accountsPermission == PackageManager.PERMISSION_GRANTED) {
            return false;
        } else {
            return true;
        }
    }

    public boolean shouldShowRequestPermissionDialog() {
        return !ActivityCompat.shouldShowRequestPermissionRationale(mActivity, Manifest.permission.READ_CALENDAR)
                || !ActivityCompat.shouldShowRequestPermissionRationale(mActivity, Manifest.permission.WRITE_CALENDAR)
                || !ActivityCompat.shouldShowRequestPermissionRationale(mActivity, Manifest.permission.GET_ACCOUNTS);
    }
}
