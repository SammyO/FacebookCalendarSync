package com.oddhov.facebookcalendarsync.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;

import com.crashlytics.android.Crashlytics;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.holder.DimenHolder;
import com.mikepenz.materialdrawer.holder.ImageHolder;
import com.mikepenz.materialdrawer.holder.StringHolder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.oddhov.facebookcalendarsync.R;
import com.oddhov.facebookcalendarsync.data.Constants;
import com.oddhov.facebookcalendarsync.data.exceptions.RealmException;
import com.oddhov.facebookcalendarsync.data.models.ActivityTransition;
import com.oddhov.facebookcalendarsync.ui_components.settings_activity.SettingsActivity;
import com.oddhov.facebookcalendarsync.ui_components.settings_activity.SettingsScreen;

import java.util.Arrays;

public class NavigationDrawerUtils {
    // region Fields
    private Context mContext;
    private DatabaseUtils mDatabaseUtils;
    private SyncAdapterUtils mSyncAdapterUtils;
    // endregion


    public NavigationDrawerUtils(Context context, DatabaseUtils databaseUtils, SyncAdapterUtils syncAdapterUtils) {
        this.mContext = context;
        this.mDatabaseUtils = databaseUtils;
        this.mSyncAdapterUtils = syncAdapterUtils;
    }

    public Drawer buildNavigationDrawer(Activity activity, Drawer.OnDrawerItemClickListener listener,
                                        Toolbar toolbar) {
        return new DrawerBuilder()
                .withActivity(activity)
                .addDrawerItems(
                        getStartStopSyncItem(),
                        getLogOutItem(),
                        getReportBugItem(),
                        getSettingsSection(),
                        getFacebookSettingsItem(),
                        getLocalSettingsItem(),
                        getSyncSettingsItem())
                .withHeader(R.layout.navigation_drawer_header)
                .withHeaderDivider(false)
                .withHeaderPadding(false)
                .withHeaderHeight(DimenHolder.fromDp(200))
                .withSelectedItem(-1)
                .withOnDrawerItemClickListener(listener)
                .withToolbar(toolbar)
                .withActionBarDrawerToggle(true)
                .build();
    }

    public void onStartStopClicked(Drawer navigationDrawer) {
        try {
            if (mDatabaseUtils.getSyncAdapterPaused()) {
                mSyncAdapterUtils.setSyncAdapterRunnable(true);
                mDatabaseUtils.setSyncAdapterPaused(false);
                navigationDrawer.updateName(Constants.STOP_START_SYNC,
                        new StringHolder(mContext.getString(R.string.navigation_drawer_stop_sync)));
                navigationDrawer.updateIcon(Constants.STOP_START_SYNC, new ImageHolder(R.drawable.ic_stop));
            } else {
                mSyncAdapterUtils.setSyncAdapterRunnable(false);
                mDatabaseUtils.setSyncAdapterPaused(true);
                navigationDrawer.updateName(Constants.STOP_START_SYNC,
                        new StringHolder(mContext.getString(R.string.navigation_drawer_start_sync)));
                navigationDrawer.updateIcon(Constants.STOP_START_SYNC, new ImageHolder(R.drawable.ic_play));
            }
        } catch (RealmException e) {
            Crashlytics.logException(e);
        }
    }

    public void onLoginLogoutClicked(CallbackManager callbackManager, Activity activity,
                                     DialogInterface.OnClickListener onClickListener,
                                     FacebookCallback<LoginResult> facebookCallback) {
        if (AccountUtils.hasEmptyOrExpiredAccessToken()) {
            LoginManager.getInstance().registerCallback(callbackManager, facebookCallback);
            LoginManager.getInstance().logInWithReadPermissions(activity, Arrays.asList("email", "public_profile", "user_events"));
        } else {
            new AlertDialog.Builder(activity)
                    .setTitle(R.string.dialog_logout_title)
                    .setMessage(R.string.dialog_logout_description)
                    .setPositiveButton(android.R.string.ok, onClickListener)
                    .setNegativeButton(android.R.string.cancel, null)
                    .show();
        }
    }

    public void onFacebookSettingsClicked(Activity activity) {
        SettingsActivity.start(activity, SettingsScreen.FACEBOOK_SETTINGS,
                ActivityTransition.NEXT.getEnter(),
                ActivityTransition.NEXT.getExit());
    }

    public void onLocalCalendarSettingsClicked(Activity activity) {
        SettingsActivity.start(activity, SettingsScreen.LOCAL_CALENDAR_SETTINGS,
                ActivityTransition.NEXT.getEnter(),
                ActivityTransition.NEXT.getExit());
    }

    public void onSyncSettingsClicked(Activity activity) {
        SettingsActivity.start(activity, SettingsScreen.SYNC_SETTINGS,
                ActivityTransition.NEXT.getEnter(),
                ActivityTransition.NEXT.getExit());
    }

    // region helper methods
    private IDrawerItem getStartStopSyncItem() {
        PrimaryDrawerItem startStopSync = null;
        try {
            startStopSync = new PrimaryDrawerItem().
                    withIdentifier(Constants.STOP_START_SYNC)
                    .withName(mDatabaseUtils.getSyncAdapterPaused() ? R.string.navigation_drawer_start_sync :
                            R.string.navigation_drawer_stop_sync)
                    .withIcon(mDatabaseUtils.getSyncAdapterPaused() ? R.drawable.ic_play : R.drawable.ic_stop)
                    .withSelectable(false);
        } catch (RealmException e) {
            Crashlytics.logException(e);
        }
        return startStopSync;
    }

    private IDrawerItem getLogOutItem() {
        return new PrimaryDrawerItem()
                .withIdentifier(Constants.LOG_IN_OUT)
                .withName(AccountUtils.hasEmptyOrExpiredAccessToken() ? R.string.navigation_drawer_log_in :
                        R.string.navigation_drawer_log_out)
                .withIcon(R.drawable.ic_logout)
                .withSelectable(false);
    }

    private IDrawerItem getReportBugItem() {
        return new PrimaryDrawerItem()
                .withIdentifier(Constants.REPORT_BUG)
                .withName(R.string.navigation_drawer_report_a_bug)
                .withIcon(R.drawable.ic_bug_report)
                .withSelectable(false);
    }

    private IDrawerItem getSettingsSection() {
        return new SectionDrawerItem()
                .withName(R.string.navigation_drawer_header_settings)
                .withSelectable(false);
    }

    private IDrawerItem getFacebookSettingsItem() {
        return new PrimaryDrawerItem()
                .withIdentifier(Constants.FACEBOOK_SETTINGS)
                .withName(R.string.navigation_drawer_facebook_settings)
                .withIcon(R.drawable.ic_facebook)
                .withSelectable(false);
    }

    private IDrawerItem getLocalSettingsItem() {
        return new PrimaryDrawerItem()
                .withIdentifier(Constants.LOCAL_CALENDAR_SETTINGS)
                .withName(R.string.navigation_drawer_local_calendar_settings)
                .withIcon(R.drawable.ic_calendar)
                .withSelectable(false);
    }

    private IDrawerItem getSyncSettingsItem() {
        return new PrimaryDrawerItem()
                .withIdentifier(Constants.SYNC_SETTINGS)
                .withName(R.string.navigation_drawer_sync_settings)
                .withIcon(R.drawable.ic_sync)
                .withSelectable(false);
    }
    // endregion
}

