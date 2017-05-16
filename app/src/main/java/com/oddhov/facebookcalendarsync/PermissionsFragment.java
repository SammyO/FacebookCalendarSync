package com.oddhov.facebookcalendarsync;


import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.crashlytics.android.Crashlytics;
import com.oddhov.facebookcalendarsync.data.Constants;
import com.oddhov.facebookcalendarsync.data.events.NavigateEvent;
import com.oddhov.facebookcalendarsync.data.exceptions.UnexpectedException;
import com.oddhov.facebookcalendarsync.utils.PermissionUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class PermissionsFragment extends Fragment implements DialogInterface.OnClickListener {

    public static final String TAG = "PermissionsFragment";

    private PermissionUtils mPermissionUtils;
    private Unbinder mUnbinder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mPermissionUtils = new PermissionUtils(getActivity());

        View view = inflater.inflate(R.layout.permissions_fragment, container, false);
        mUnbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!mPermissionUtils.needsPermissions()) {
            EventBus.getDefault().post(new NavigateEvent());
        }
    }

    @Override
    public void onDestroyView() {
        mUnbinder.unbind();
        super.onDestroyView();
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
                EventBus.getDefault().post(new NavigateEvent());
                return;
            }

            if (mPermissionUtils.shouldShowRequestPermissionDialog()) {
                showRequestPermissionRationale(R.string.request_account_and_calendar_permission_description);
            }
        }
    }
    //endregion

    //region VI methods
    @OnClick(R.id.btnGrantPermissions)
    public void onGrantPermissionsClicked() {
        try {
            String[] permissionsNeeded = checkPermissionsAndRequest();
            requestPermissions(permissionsNeeded,
                    Constants.REQUEST_READ_WRITE_CALENDAR_GET_ACCOUNT_PERMISSIONS);
        } catch (UnexpectedException e) {
            Crashlytics.logException(new UnexpectedException("PermissionsFragment", "No permissions needed"));
            EventBus.getDefault().post(new NavigateEvent());
        }
    }
    //endregion

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
