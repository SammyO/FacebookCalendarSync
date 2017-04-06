package com.oddhov.facebookcalendarsync;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.facebook.login.LoginManager;
import com.oddhov.facebookcalendarsync.utils.AccountUtils;
import com.oddhov.facebookcalendarsync.utils.NotificationUtils;

public class MainActivity extends AppCompatActivity implements DialogInterface.OnClickListener {
    //region Lifecycle Methods
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_logout:
                new AlertDialog.Builder(this)
                        .setTitle(R.string.dialog_logout_title)
                        .setMessage(R.string.dialog_logout_description)
                        .setPositiveButton(android.R.string.ok, this)
                        .setNegativeButton(android.R.string.cancel, null)
                        .show();
                return true;
            case R.id.action_settings:
                SettingsActivity.start(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    // endregion

    // region DialogInterface.OnClickListener
    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        if (i == DialogInterface.BUTTON_POSITIVE) {
            LoginManager.getInstance().logOut();
            navigate();
        }
    }
    //endregion

    // region Helper methods UI
    private void navigate() {
        if (needsPermissions()) {
            replaceFragment(R.id.fragment_container, new PermissionsFragment(), PermissionsFragment.TAG);
        } else if (AccountUtils.hasEmptyOrExpiredAccessToken()) {
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
    // endregion
}

