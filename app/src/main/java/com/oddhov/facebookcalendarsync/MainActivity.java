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
import android.provider.ContactsContract;
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
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.oddhov.facebookcalendarsync.data.Constants;
import com.oddhov.facebookcalendarsync.utils.AccountManagerUtils;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,
        AccessToken.AccessTokenRefreshCallback, DialogInterface.OnClickListener {
    //region Fields
    private Button btnLogOut;
    private Button btnSyncAll;
    private Button btnSyncUpcoming;
    private Button btnRetrieveToken;
    private Button btnRevokeToken;

    private boolean mSyncOnlyUpcomingEvents = false;

    private AccessTokenTracker mAccessTokenTracker;
    //endregion

    //region Lifecycle Methods
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_main);

        setupViews();
        setupFacebook();
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
            for (int grantResult : grantResults) {
                if (grantResult == PackageManager.PERMISSION_DENIED) {
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_CALENDAR)) {
                        showRequestPermissionRationale(R.string.request_calendar_permission_description);
                        return;
                    }
                }
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
            case R.id.btnRetrieveToken:
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.GET_ACCOUNTS},
                            Constants.REQUEST_ACCOUNTS_PERMISSION);
                    return;
                }
                String token = AccountManagerUtils.retrieveTokenFromAuthManager(this);
                Log.e("MainActivity", "Facebook access token: " + token);

                break;
            case R.id.btnLogOut:
                // TODO move this to settings
                // TODO proceed with "Are you sure?" alert
                LoginManager.getInstance().logOut();
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.GET_ACCOUNTS) == PackageManager.PERMISSION_GRANTED) {
                    AccountManagerUtils.removeFromAuthManager(this);
                }
                startLoginActivity();
                break;
            case R.id.btnRevokeToken:
                revokeFacebookToken();
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
        AccountManagerUtils.updateAuthManager(MainActivity.this, accessToken);
    }

    private void removeTokenFromAccountManagerIfPresent() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if (AccountManagerUtils.retrieveTokenFromAuthManager(this) != null) {
            AccountManagerUtils.removeFromAuthManager(this);
        }
    }
    //endregion

    //region UI Helper methods
    private void showRequestPermissionRationale(int message) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.request_permissions_title)
                .setMessage(message)
                .setPositiveButton(R.string.word_app_info, this)
                .setCancelable(true)
                .show();
    }

    private void setupViews() {
        btnLogOut = (Button) findViewById(R.id.btnLogOut);
        btnLogOut.setOnClickListener(this);

        btnSyncAll = (Button) findViewById(R.id.btnSynAll);
        btnSyncUpcoming = (Button) findViewById(R.id.btnSyncUpcoming);
        btnRetrieveToken = (Button) findViewById(R.id.btnRetrieveToken);
        btnRevokeToken = (Button) findViewById(R.id.btnRevokeToken);

        btnSyncAll.setOnClickListener(this);
        btnSyncUpcoming.setOnClickListener(this);
        btnRetrieveToken.setOnClickListener(this);
        btnRevokeToken.setOnClickListener(this);
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

    private void revokeFacebookToken() {
        new GraphRequest(AccessToken.getCurrentAccessToken(), "/me/permissions", null, HttpMethod.DELETE,
                new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse response) {
                        boolean isSuccess = false;
                        try {
                            isSuccess = response.getJSONObject().getBoolean("success");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (isSuccess && response.getError() == null) {
                            Log.e("MainActivity", "Facebook token revoked");
                        }

                    }
                }).executeAsync();
    }

    private void checkPermissionsAndStartSyncAdapter() {
        int readCalendarPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR);
        int writeCalendarPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR);

        if (readCalendarPermission == PackageManager.PERMISSION_GRANTED &&
                writeCalendarPermission == PackageManager.PERMISSION_GRANTED) {
            startSyncAdapter(mSyncOnlyUpcomingEvents);
        } else {
            List<String> permissionsNeeded = new ArrayList<>();
            if (readCalendarPermission == PackageManager.PERMISSION_DENIED) {
                permissionsNeeded.add(Manifest.permission.READ_CALENDAR);
            }
            if (writeCalendarPermission == PackageManager.PERMISSION_DENIED) {
                permissionsNeeded.add(Manifest.permission.WRITE_CALENDAR);
            }
            ActivityCompat.requestPermissions(this, permissionsNeeded.toArray(new String[permissionsNeeded.size()]),
                    Constants.REQUEST_READ_WRITE_CALENDAR_PERMISSION);
        }
    }

    private void startSyncAdapter(boolean onlyUpcomingEvents) {
        // Pass the settings flags by inserting them in a bundle
        Bundle settingsBundle = new Bundle();
        settingsBundle.putBoolean(
                ContentResolver.SYNC_EXTRAS_MANUAL, true);
        settingsBundle.putBoolean(
                ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        /*
         * Request the sync for the default account, authority, and
         * manual sync settings
         */
        Account account = new Account(Constants.ACCOUNT_NAME, Constants.ACCOUNT_TYPE);
        Bundle bundle = new Bundle();
        bundle.putBoolean(Constants.SYNC_ONLY_UPCOMING_EVENTS, onlyUpcomingEvents);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(account, CalendarContract.AUTHORITY, bundle);
        ContentResolver.setSyncAutomatically(account, ContactsContract.AUTHORITY, true);
    }
    //endregion
}
