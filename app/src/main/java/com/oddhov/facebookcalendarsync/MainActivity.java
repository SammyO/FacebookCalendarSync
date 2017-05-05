package com.oddhov.facebookcalendarsync;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.internal.CallbackManagerImpl;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.holder.DimenHolder;
import com.mikepenz.materialdrawer.holder.StringHolder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.oddhov.facebookcalendarsync.data.Constants;
import com.oddhov.facebookcalendarsync.data.events.NavigateEvent;
import com.oddhov.facebookcalendarsync.utils.AccountUtils;
import com.oddhov.facebookcalendarsync.utils.PermissionUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements DialogInterface.OnClickListener,
        Drawer.OnDrawerItemClickListener, FacebookCallback<LoginResult> {
    //region Fields
    private PermissionUtils mPermissionUtils;
    private Drawer mNavigationDrawer;
    private CallbackManager mCallbackManager;
    private Toolbar mToolbar;
    //endregion

    //region Lifecycle Methods
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPermissionUtils = new PermissionUtils(this);

        setContentView(R.layout.activity_main);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        setupNavigationDrawer();

        onNavigateEvent(new NavigateEvent());
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        if (requestCode == CallbackManagerImpl.RequestCodeOffset.Login.toRequestCode()) {
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
    // endregion

    // region DialogInterface.OnClickListener
    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        if (i == DialogInterface.BUTTON_POSITIVE) {
            LoginManager.getInstance().logOut();
            mNavigationDrawer.updateName(Constants.LOG_IN_OUT, new StringHolder(getString(R.string.navigation_drawer_log_in)));
            onNavigateEvent(new NavigateEvent());
        }
    }
    // endregion

    // region Facebook Listener methods
    @Override
    public void onSuccess(LoginResult loginResult) {
        mNavigationDrawer.updateName(Constants.LOG_IN_OUT, new StringHolder(getString(R.string.navigation_drawer_log_out)));
    }

    @Override
    public void onCancel() {
        Toast toast = Toast.makeText(this, R.string.login_again, Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    public void onError(FacebookException error) {
        Toast toast = Toast.makeText(this, R.string.login_again, Toast.LENGTH_SHORT);
        toast.show();
    }
    // endregion

    // region Drawer.OnDrawerItemClickListener
    @Override
    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
        switch ((int) drawerItem.getIdentifier()) {
            case Constants.STOP_START_SYNC:

                return true;
            case Constants.LOG_IN_OUT:
                if (AccountUtils.hasEmptyOrExpiredAccessToken()) {
                    mCallbackManager = CallbackManager.Factory.create();
                    LoginManager.getInstance().registerCallback(mCallbackManager, this);
                    LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email", "public_profile", "user_events"));
                } else {
                    new AlertDialog.Builder(this)
                            .setTitle(R.string.dialog_logout_title)
                            .setMessage(R.string.dialog_logout_description)
                            .setPositiveButton(android.R.string.ok, this)
                            .setNegativeButton(android.R.string.cancel, null)
                            .show();
                }
                mNavigationDrawer.closeDrawer();
                return true;
            case Constants.REPORT_BUG:

                return true;
            case Constants.FACEBOOK_SETTINGS:

                return true;
            case Constants.LOCAL_CALENDAR_SETTINGS:

                return true;
            case Constants.SYNC_SETTINGS:

                return true;
            default:

                return false;
        }
    }
    // endregion

    // region EventBus
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNavigateEvent(NavigateEvent event) {
        if (mPermissionUtils.needsPermissions()) {
            replaceFragment(R.id.fragment_container, new PermissionsFragment(), PermissionsFragment.TAG);
        } else if (AccountUtils.hasEmptyOrExpiredAccessToken()) {
            replaceFragment(R.id.fragment_container, new LoginFragment(), LoginFragment.TAG);
        } else {
            replaceFragment(R.id.fragment_container, new SyncFragment(), SyncFragment.TAG);
        }
    }
    // endregion

    // region Helper methods UI
    private void setupNavigationDrawer() {
        // TODO change state of button based on start/stop state
        PrimaryDrawerItem startStopSync = new PrimaryDrawerItem().withIdentifier(Constants.STOP_START_SYNC).withName(
                R.string.navigation_drawer_stop_sync).withIcon(R.drawable.ic_stop).withSelectable(false);
        PrimaryDrawerItem logOut;
        if (AccountUtils.hasEmptyOrExpiredAccessToken()) {
            logOut = new PrimaryDrawerItem().withIdentifier(Constants.LOG_IN_OUT).withName(
                    R.string.navigation_drawer_log_in).withIcon(R.drawable.ic_logout).withSelectable(false);
        } else {
            logOut = new PrimaryDrawerItem().withIdentifier(Constants.LOG_IN_OUT).withName(
                    R.string.navigation_drawer_log_out).withIcon(R.drawable.ic_logout).withSelectable(false);
        }
        PrimaryDrawerItem reportBug = new PrimaryDrawerItem().withIdentifier(Constants.REPORT_BUG).withName(
                R.string.navigation_drawer_report_a_bug).withIcon(R.drawable.ic_bug_report).withSelectable(false);
        SectionDrawerItem settingsSection = new SectionDrawerItem()
                .withName(R.string.navigation_drawer_header_settings).withSelectable(false);
        PrimaryDrawerItem facebookSettings = new PrimaryDrawerItem().withIdentifier(Constants.FACEBOOK_SETTINGS).withName(
                R.string.navigation_drawer_facebook_settings).withIcon(R.drawable.ic_facebook).withSelectable(false);
        PrimaryDrawerItem localCalendarSettings = new PrimaryDrawerItem().withIdentifier(Constants.LOCAL_CALENDAR_SETTINGS).withName(
                R.string.navigation_drawer_local_calendar_settings).withIcon(R.drawable.ic_calendar).withSelectable(false);
        PrimaryDrawerItem syncSettings = new PrimaryDrawerItem().withIdentifier(Constants.SYNC_SETTINGS).withName(
                R.string.navigation_drawer_sync_settings).withIcon(R.drawable.ic_sync).withSelectable(false);

        mNavigationDrawer = new DrawerBuilder()
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
                .withHeaderHeight(DimenHolder.fromDp(200))
                .withSelectedItem(-1)
                .withOnDrawerItemClickListener(this)
                .withToolbar(mToolbar)
                .withActionBarDrawerToggle(true)
                .build();

        if (getSupportActionBar() !=  null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            mNavigationDrawer.getActionBarDrawerToggle().setDrawerIndicatorEnabled(true);
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
}

