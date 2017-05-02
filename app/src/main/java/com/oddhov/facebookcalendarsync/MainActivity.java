package com.oddhov.facebookcalendarsync;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.facebook.login.LoginManager;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.ContainerDrawerItem;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.oddhov.facebookcalendarsync.utils.AccountUtils;
import com.oddhov.facebookcalendarsync.utils.PermissionUtils;

public class MainActivity extends AppCompatActivity implements DialogInterface.OnClickListener,
        NavigationListener {
    //region Fields
    private PermissionUtils mPermissionUtils;
    //endregion

    //region Lifecycle Methods
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPermissionUtils = new PermissionUtils(this);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setupNavigationDrawer();

        navigate();
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

    // region NavigationListener
    @Override
    public void navigate() {
        if (mPermissionUtils.needsPermissions()) {
            replaceFragment(R.id.fragment_container, new PermissionsFragment(), PermissionsFragment.TAG);
        } else if (AccountUtils.hasEmptyOrExpiredAccessToken()) {
            replaceFragment(R.id.fragment_container, new LoginFragment(), LoginFragment.TAG);
        } else {
            replaceFragment(R.id.fragment_container, new SyncFragment(), SyncFragment.TAG);
        }
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
    private void setupNavigationDrawer() {
        SectionDrawerItem settingsSection = new SectionDrawerItem()
                .withName(R.string.navigation_drawer_header_settings);
        PrimaryDrawerItem facebookSettings = new PrimaryDrawerItem().withIdentifier(1).withName(
                R.string.navigation_drawer_facebook_settings).withIcon(R.drawable.ic_facebook);
        PrimaryDrawerItem localCalendarSettings = new PrimaryDrawerItem().withIdentifier(2).withName(
                R.string.navigation_drawer_local_calendar_settings).withIcon(R.drawable.ic_calendar);
        PrimaryDrawerItem syncSettings = new PrimaryDrawerItem().withIdentifier(3).withName(
                R.string.navigation_drawer_sync_settings).withIcon(R.drawable.ic_sync);
        PrimaryDrawerItem startStopSync = new PrimaryDrawerItem().withIdentifier(4).withName(
                R.string.navigation_drawer_stop_sync).withIcon(R.drawable.ic_stop);
        PrimaryDrawerItem logOut = new PrimaryDrawerItem().withIdentifier(5).withName(
                R.string.navigation_drawer_log_out).withIcon(R.drawable.ic_logout);
        PrimaryDrawerItem reportBug = new PrimaryDrawerItem().withIdentifier(6).withName(
                R.string.navigation_drawer_report_a_bug).withIcon(R.drawable.ic_bug_report);

        new DrawerBuilder()
                .withActivity(this)
                .addDrawerItems(
                        startStopSync,
                        logOut,
                        reportBug,
                        settingsSection,
                        facebookSettings,
                        localCalendarSettings,
                        syncSettings)
                .withHeader(R.layout.navigation_drawer_header)
                .withHeaderDivider(false)
                .withHeaderPadding(false)
                .withSelectedItem(-1)
                .build();
    }

    private void replaceFragment(int containerId, Fragment fragment, String tag) {
        if (getSupportFragmentManager() != null) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(containerId, fragment, tag);
            fragmentTransaction.commit();
        }
    }
    //endregion
}

