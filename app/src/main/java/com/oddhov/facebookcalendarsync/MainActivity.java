package com.oddhov.facebookcalendarsync;


import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import com.facebook.AccessToken;
import com.oddhov.facebookcalendarsync.utils.AccountManagerUtils;

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

public class MainActivity extends AppCompatActivity {

    //region Lifecycle Methods
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();

        navigate();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    //endregion

    // region Helper methods UI
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
        if (getSupportFragmentManager() != null) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(containerId, fragment, tag);
            fragmentTransaction.commit();
        }
    }
    //endregion

    // region Helper methods validation
    private boolean needsPermissions() {
        int readCalendarPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR);
        int writeCalendarPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR);
        int accountsPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.GET_ACCOUNTS);

        if (readCalendarPermission == PackageManager.PERMISSION_GRANTED &&
                writeCalendarPermission == PackageManager.PERMISSION_GRANTED &&
                accountsPermission == PackageManager.PERMISSION_GRANTED) {
            return false;
        } else {
            return true;
        }
    }

    private boolean hasEmptyOrExpiredAccessToken() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken == null || accessToken.isExpired();
    }
    // endregion
}
