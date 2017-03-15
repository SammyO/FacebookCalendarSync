package com.oddhov.facebookcalendarsync;

import android.Manifest;
import android.accounts.Account;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.oddhov.facebookcalendarsync.data.Constants;
import com.oddhov.facebookcalendarsync.utils.AccountManagerUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * General TODO:
 * Use throwing exceptions to handle edge cases
 * Missing permissions should invoke notification
 * Move logout to settings
 * In settings, provide option to enable sync all, or sync future events (and store in SharedPreferences)
 * In settings, provide option to set account?
 * In main screen show time of last sync (if possible)
 * In main screen show button to sync now
 * Presenters
 * Butterknife
 * Realm? (just for fun)
 * Eventbus? (just for fun)
 * RxJava? (just for fun)
 * Gradle check code scripts
 * Unit tests
 */

public class MainActivityOld extends AppCompatActivity implements View.OnClickListener,
        AccessToken.AccessTokenRefreshCallback, DialogInterface.OnClickListener {
    //region Fields
    private Button btnLogOut;
    private Button btnSyncAll;
    private Button btnSyncUpcoming;

    private boolean mSyncOnlyUpcomingEvents = false;

    private AccessTokenTracker mAccessTokenTracker;
    private AccountManagerUtils mAccountManagerUtils;
    //endregion

    //region Lifecycle Methods
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_main_old);

        setupViews();
        setupFacebook();

        mAccountManagerUtils = new AccountManagerUtils(this);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (verifyAccessTokenNotPresentOrInvalid(AccessToken.getCurrentAccessToken())) {
            // If we don't have permissions to update the token, set the state to logged out
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {
                setStateToLoggedOut();
                startLoginActivity();
                return;
            }
            AccessToken.refreshCurrentAccessTokenAsync(this);
        }
        mAccessTokenTracker.startTracking();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mAccessTokenTracker.stopTracking();
    }
    //endregion

    //region Activity Methods
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        if (grantResults.length == 0) {
            return;
        }
        if (requestCode == Constants.REQUEST_ACCOUNTS_PERMISSION) {
            for (int grantResult : grantResults) {
                if (grantResult == PackageManager.PERMISSION_DENIED) {
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.GET_ACCOUNTS)) {
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
                startSyncAdapter(mSyncOnlyUpcomingEvents);
                return;
            }

            if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_CALENDAR)
                    || !ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.GET_ACCOUNTS)) {
                showRequestPermissionRationale(R.string.request_account_and_calendar_permission_description);
            }
        }
    }
    //endregion

    //region OnClickListeners
    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        startSettingsActivity();
    }
    //endregion

    // region Facebook refreshToken callback methods
    @Override
    public void OnTokenRefreshed(AccessToken accessToken) {
        updateAccountManagerToken(accessToken);
    }

    @Override
    public void OnTokenRefreshFailed(FacebookException exception) {
        setStateToLoggedOut();
        startLoginActivity();
    }
    //endregion

    //region onClickListeners
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnLogOut:
                // TODO move this to settings
                // TODO proceed with "Are you sure?" alert
                LoginManager.getInstance().logOut();
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.GET_ACCOUNTS) == PackageManager.PERMISSION_GRANTED) {
                    mAccountManagerUtils.removeFromAuthManager();
                }
                startLoginActivity();
                break;
            case R.id.btnSynAll:
                mSyncOnlyUpcomingEvents = false;
                checkPermissionsAndStartSyncAdapter();
                break;
            case R.id.btnSyncUpcoming:
                mSyncOnlyUpcomingEvents = true;
                checkPermissionsAndStartSyncAdapter();
                break;
        }
    }
    //endregion

    // region AccessToken Helper Methods
    private boolean verifyAccessTokenNotPresentOrInvalid(AccessToken accessToken) {
        return accessToken == null || accessToken.isExpired();
    }

    private void updateAccountManagerToken(AccessToken accessToken) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {
            setStateToLoggedOut();
            return;
        }
        mAccountManagerUtils.updateAuthManager(accessToken);
    }

    private void removeTokenFromAccountManagerIfPresent() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if (mAccountManagerUtils.retrieveTokenFromAuthManager() != null) {
            mAccountManagerUtils.removeFromAuthManager();
        }
    }
    //endregion

    //region UI Helper methods
    private void showRequestPermissionRationale(int message) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.request_permissions_title)
                .setMessage(message)
                .setPositiveButton(R.string.word_app_info, this)
                .setNegativeButton(R.string.word_cancel, null)
                .show();
    }

    private void setupViews() {
        btnLogOut = (Button) findViewById(R.id.btnLogOut);
        btnLogOut.setOnClickListener(this);

        btnSyncAll = (Button) findViewById(R.id.btnSynAll);
        btnSyncUpcoming = (Button) findViewById(R.id.btnSyncUpcoming);

        btnSyncAll.setOnClickListener(this);
        btnSyncUpcoming.setOnClickListener(this);
    }

    private void startLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    private void startSettingsActivity() {
        Intent myAppSettings = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.parse("package:" + getPackageName()));
        myAppSettings.addCategory(Intent.CATEGORY_DEFAULT);
        myAppSettings.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivityForResult(myAppSettings, Constants.REQUEST_APP_SETTINGS);
    }
    //endregion

    //region Helper methods
    private void setStateToLoggedOut() {
        LoginManager.getInstance().logOut();
        removeTokenFromAccountManagerIfPresent();
    }

    private void setupFacebook() {
        mAccessTokenTracker = new AccessTokenTracker() {
            @Override
            protected final void onCurrentAccessTokenChanged(AccessToken oldAccessToken,
                                                             AccessToken currentAccessToken) {
                if (currentAccessToken != null) {
                    updateAccountManagerToken(currentAccessToken);
                }
            }
        };
    }

    private void checkPermissionsAndStartSyncAdapter() {

        int readCalendarPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR);
        int writeCalendarPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR);
        int accountsPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.GET_ACCOUNTS);

        if (readCalendarPermission == PackageManager.PERMISSION_GRANTED &&
                writeCalendarPermission == PackageManager.PERMISSION_GRANTED &&
                accountsPermission == PackageManager.PERMISSION_GRANTED) {
            startSyncAdapter(mSyncOnlyUpcomingEvents);
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
            ActivityCompat.requestPermissions(this, permissionsNeeded.toArray(new String[permissionsNeeded.size()]),
                    Constants.REQUEST_READ_WRITE_CALENDAR_PERMISSION);
        }
    }

    private void startSyncAdapter(boolean onlyUpcomingEvents) {
        Log.e("MainActivityOld", "startSyncAdapter");
        Account account = new Account(Constants.ACCOUNT_NAME, Constants.ACCOUNT_TYPE);
        Bundle bundle = new Bundle();
        bundle.putBoolean(Constants.SYNC_ONLY_UPCOMING_EVENTS, onlyUpcomingEvents);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
//        ContentResolver.setIsSyncable(account, Constants.CALENDAR_AUTHORITY, 1);
//        ContentResolver.addPeriodicSync(account, Constants.CALENDAR_AUTHORITY, Bundle.EMPTY, Constants.SYNC_INTERVAL);
//        ContentResolver.setSyncAutomatically(account, Constants.CALENDAR_AUTHORITY, true);
        ContentResolver.requestSync(account, CalendarContract.AUTHORITY, bundle);
    }
    //endregion
}
