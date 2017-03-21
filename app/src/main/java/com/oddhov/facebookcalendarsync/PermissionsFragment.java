package com.oddhov.facebookcalendarsync;


import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.facebook.AccessToken;
import com.oddhov.facebookcalendarsync.data.Constants;
import com.oddhov.facebookcalendarsync.utils.AccountManagerUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PermissionsFragment extends Fragment implements View.OnClickListener,
        DialogInterface.OnClickListener {

    public static final String TAG = "PermissionsFragment";

    private AccountManagerUtils mAccountManagerUtils;
    private Button btnGrantPermissions;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.permissions_fragment, container, false);
        btnGrantPermissions = (Button) view.findViewById(R.id.btnGrantPermissions);
        btnGrantPermissions.setOnClickListener(this);

        mAccountManagerUtils = new AccountManagerUtils(getActivity());

        return view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        if (grantResults.length == 0) {
            return;
        }
        if (requestCode == Constants.REQUEST_ACCOUNTS_PERMISSION) {
            for (int grantResult : grantResults) {
                if (grantResult == PackageManager.PERMISSION_DENIED) {
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.GET_ACCOUNTS)) {
                        showRequestPermissionRationale(R.string.request_accounts_permission_description);
                        return;
                    }
                }
            }
        } else if (requestCode == Constants.REQUEST_READ_WRITE_CALENDAR_PERMISSION) {
            Map<String, Integer> permissionResults = new HashMap<>();
            permissionResults.put(Manifest.permission.WRITE_CALENDAR, PackageManager.PERMISSION_GRANTED);
            permissionResults.put(Manifest.permission.GET_ACCOUNTS, PackageManager.PERMISSION_GRANTED);
            for (int i = 0; i < permissions.length; i++) {
                permissionResults.put(permissions[i], grantResults[i]);
            }
            if (permissionResults.get(Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED
                    && permissionResults.get(Manifest.permission.GET_ACCOUNTS) == PackageManager.PERMISSION_GRANTED) {
                navigate();
                return;
            }

            if (!ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.WRITE_CALENDAR)
                    || !ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.GET_ACCOUNTS)) {
                showRequestPermissionRationale(R.string.request_account_and_calendar_permission_description);
            }
        }
    }
    //endregion

    // region OnClickListeners
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnGrantPermissions:
                checkPermissionsAndRequest();
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
    // TODO remove this and the next when EventBus is here
    private void navigate() {
        if (needsPermissions()) {
            replaceFragment(R.id.fragment_container, new PermissionsFragment(), PermissionsFragment.TAG);
        } else if (hasEmptyOrExpiredAccessToken()) {
            replaceFragment(R.id.fragment_container, new LoginFragment(), LoginFragment.TAG);
        } else {
            replaceFragment(R.id.fragment_container, new SyncFragment(), SyncFragment.TAG);
        }
    }

    private void replaceFragment(int containerId, Fragment fragment, String tag) {
        if (getFragmentManager() != null) {
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.replace(containerId, fragment, tag);
            fragmentTransaction.commit();
        }
    }

    private void showRequestPermissionRationale(int message) {
        new AlertDialog.Builder(getActivity())
                .setTitle(R.string.request_permissions_title)
                .setMessage(message)
                .setPositiveButton(R.string.word_app_info, this)
                .setNegativeButton(R.string.word_cancel, null)
                .show();
    }

    private void startSettingsActivity() {
        Intent myAppSettings = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.parse("package:" + getActivity().getPackageName()));
        myAppSettings.addCategory(Intent.CATEGORY_DEFAULT);
        myAppSettings.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivityForResult(myAppSettings, Constants.REQUEST_APP_SETTINGS);
    }
    //endregion

    // region Helper methods validation
    private void checkPermissionsAndRequest() {

        int readCalendarPermission = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CALENDAR);
        int writeCalendarPermission = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_CALENDAR);
        int accountsPermission = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.GET_ACCOUNTS);

        if (readCalendarPermission == PackageManager.PERMISSION_GRANTED &&
                writeCalendarPermission == PackageManager.PERMISSION_GRANTED &&
                accountsPermission == PackageManager.PERMISSION_GRANTED) {
            navigate();
        } else {
            if (shouldShowRequestPermissionDialog()) {
                showRequestPermissionRationale(R.string.request_account_and_calendar_permission_description);
                return;
            }

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
            ActivityCompat.requestPermissions(getActivity(), permissionsNeeded.toArray(new String[permissionsNeeded.size()]),
                    Constants.REQUEST_READ_WRITE_CALENDAR_PERMISSION);
        }
    }

    private boolean needsPermissions() {
        int readCalendarPermission = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CALENDAR);
        int writeCalendarPermission = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_CALENDAR);
        int accountsPermission = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.GET_ACCOUNTS);

        if (readCalendarPermission == PackageManager.PERMISSION_GRANTED &&
                writeCalendarPermission == PackageManager.PERMISSION_GRANTED &&
                accountsPermission == PackageManager.PERMISSION_GRANTED) {
            return false;
        } else {
            return true;
        }
    }

    private boolean shouldShowRequestPermissionDialog() {
        return !ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.READ_CALENDAR)
                || !ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.WRITE_CALENDAR)
                || !ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.GET_ACCOUNTS);
    }

    private boolean hasEmptyOrExpiredAccessToken() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if (accessToken == null || accessToken.isExpired()) {
            mAccountManagerUtils.removeFromAuthManager();
            return true;
        }
        return false;
    }
    // endregion
}
