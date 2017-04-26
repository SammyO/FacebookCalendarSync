package com.oddhov.facebookcalendarsync;


import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.crashlytics.android.Crashlytics;
import com.oddhov.facebookcalendarsync.data.Constants;
import com.oddhov.facebookcalendarsync.data.exceptions.UnexpectedException;
import com.oddhov.facebookcalendarsync.utils.PermissionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PermissionsFragment extends Fragment implements View.OnClickListener,
        DialogInterface.OnClickListener {

    public static final String TAG = "PermissionsFragment";

    private Button btnGrantPermissions;
    private NavigationListener mNavigationListenerCallback;
    private PermissionUtils mPermissionUtils;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mPermissionUtils = new PermissionUtils(getActivity());

        View view = inflater.inflate(R.layout.permissions_fragment, container, false);
        btnGrantPermissions = (Button) view.findViewById(R.id.btnGrantPermissions);
        btnGrantPermissions.setOnClickListener(this);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mNavigationListenerCallback = (NavigationListener) context;
        } catch (ClassCastException e) {
            Log.e(TAG, context.toString()
                    + " must implement LoginNavigationListener");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!mPermissionUtils.needsPermissions()) {
            mNavigationListenerCallback.navigate();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        if (grantResults.length == 0) {
            return;
        }
        if (requestCode == Constants.REQUEST_READ_WRITE_CALENDAR_GET_ACCOUNT_PERMISSIONS) {
            Map<String, Integer> permissionResults = new HashMap<>();
            permissionResults.put(Manifest.permission.WRITE_CALENDAR, PackageManager.PERMISSION_GRANTED);
            permissionResults.put(Manifest.permission.GET_ACCOUNTS, PackageManager.PERMISSION_GRANTED);
            for (int i = 0; i < permissions.length; i++) {
                permissionResults.put(permissions[i], grantResults[i]);
            }
            if (permissionResults.get(Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED
                    && permissionResults.get(Manifest.permission.GET_ACCOUNTS) == PackageManager.PERMISSION_GRANTED) {
                mNavigationListenerCallback.navigate();
                return;
            }

            if (mPermissionUtils.shouldShowRequestPermissionDialog()) {
                showRequestPermissionRationale(R.string.request_account_and_calendar_permission_description);
            }
        }
//        if (requestCode == Constants.REQUEST_ACCOUNTS_PERMISSION) {
//            for (int grantResult : grantResults) {
//                if (grantResult == PackageManager.PERMISSION_DENIED) {
//                    if (!ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.GET_ACCOUNTS)) {
//                        showRequestPermissionRationale(R.string.request_accounts_permission_description);
//                        return;
//                    }
//                }
//            }
//        } else if (requestCode == Constants.REQUEST_READ_WRITE_CALENDAR_GET_ACCOUNT_PERMISSIONS) {
//            Map<String, Integer> permissionResults = new HashMap<>();
//            permissionResults.put(Manifest.permission.WRITE_CALENDAR, PackageManager.PERMISSION_GRANTED);
//            permissionResults.put(Manifest.permission.GET_ACCOUNTS, PackageManager.PERMISSION_GRANTED);
//            for (int i = 0; i < permissions.length; i++) {
//                permissionResults.put(permissions[i], grantResults[i]);
//            }
//            if (permissionResults.get(Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED
//                    && permissionResults.get(Manifest.permission.GET_ACCOUNTS) == PackageManager.PERMISSION_GRANTED) {
//                navigate();
//                return;
//            }
//
//            if (!ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.WRITE_CALENDAR)
//                    || !ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.GET_ACCOUNTS)) {
//                showRequestPermissionRationale(R.string.request_account_and_calendar_permission_description);
//            }
//        }
    }
    //endregion

    // region OnClickListeners
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnGrantPermissions:
                try {
                    String[] permissionsNeeded = checkPermissionsAndRequest();
                    requestPermissions(permissionsNeeded,
                        Constants.REQUEST_READ_WRITE_CALENDAR_GET_ACCOUNT_PERMISSIONS);
                } catch (UnexpectedException e) {
                    Crashlytics.logException(new UnexpectedException("PermissionsFragment", "No permissions needed"));
                    mNavigationListenerCallback.navigate();
                }
                break;
        }
    }
    // endregion


    //region OnClickListeners
    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        startSettingsActivity();
    }
    //endregion

    // region Helper methods UI
    private void showRequestPermissionRationale(int message) {
        new AlertDialog.Builder(getContext())
                .setTitle(R.string.request_permissions_title)
                .setMessage(message)
                .setPositiveButton(R.string.word_app_info, this)
                .setNegativeButton(R.string.word_cancel, null)
                .show();
    }

    private void startSettingsActivity() {
        Intent myAppSettings = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.parse("package:" + getContext().getPackageName()));
        myAppSettings.addCategory(Intent.CATEGORY_DEFAULT);
        myAppSettings.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivityForResult(myAppSettings, Constants.REQUEST_APP_SETTINGS);
    }
    //endregion

    // region Helper methods validation
    private String[] checkPermissionsAndRequest() throws UnexpectedException {
        int readCalendarPermission = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CALENDAR);
        int writeCalendarPermission = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_CALENDAR);
        int accountsPermission = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.GET_ACCOUNTS);

        if (readCalendarPermission == PackageManager.PERMISSION_GRANTED &&
                writeCalendarPermission == PackageManager.PERMISSION_GRANTED &&
                accountsPermission == PackageManager.PERMISSION_GRANTED) {
            throw new UnexpectedException("PermissionsFragment", "No permissions needed");
        } else {
            List<String> permissionsNeeded = new ArrayList<>();
            if (readCalendarPermission == PackageManager.PERMISSION_DENIED) {
                permissionsNeeded.add(Manifest.permission.READ_CALENDAR);
            }
            if (writeCalendarPermission == PackageManager.PERMISSION_DENIED) {
                permissionsNeeded.add(Manifest.permission.WRITE_CALENDAR);
            }
            if (accountsPermission == PackageManager.PERMISSION_DENIED) {
                permissionsNeeded.add(Manifest.permission.GET_ACCOUNTS);
            }
            return permissionsNeeded.toArray(new String[permissionsNeeded.size()]);
        }
    }
    // endregion
}
