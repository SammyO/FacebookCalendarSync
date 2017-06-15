package com.oddhov.facebookcalendarsync.ui_components.settings_activity;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.oddhov.facebookcalendarsync.R;
import com.oddhov.facebookcalendarsync.app.ActivityModule;
import com.oddhov.facebookcalendarsync.app.FacebookCalendarSyncApplication;
import com.oddhov.facebookcalendarsync.data.Constants;
import com.oddhov.facebookcalendarsync.data.events.NavigateBackEvent;

import org.greenrobot.eventbus.EventBus;

public class SettingsActivity extends AppCompatActivity {

    private SettingsActivityComponent mSettingsActivityComponent;
    private int mTitle;

    public static void start(Activity activity, SettingsScreen settingsScreen, int enterAnim, int exitAnim) {
        Intent intent = new Intent(activity, SettingsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(Constants.SETTINGS_SCREEN, settingsScreen.ordinal());
        activity.startActivity(intent);
        activity.overridePendingTransition(enterAnim, exitAnim);
    }

    // region Lifecycle Methods
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeInjector();

        setContentView(R.layout.activity_settings);

        setupToolbar();
        if (savedInstanceState == null) {
            setupScreen(getIntent().getIntExtra(Constants.SETTINGS_SCREEN, 0));
        } else {
            mTitle = savedInstanceState.getInt(Constants.TITLE);
            setTitle(mTitle);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(Constants.TITLE, mTitle);
    }
    // endregion

    // region Activity methods
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return false;
    }
    @Override
    public void onBackPressed() {
        EventBus.getDefault().post(new NavigateBackEvent());
    }
    // endregion

    // region Helper Methods Dagger
    public SettingsActivityComponent getComponent() {
        return this.mSettingsActivityComponent;
    }

    private void initializeInjector() {
        mSettingsActivityComponent = DaggerSettingsActivityComponent.builder()
                .applicationComponent(((FacebookCalendarSyncApplication) getApplication()).getApplicationComponent())
                .activityModule(new ActivityModule(this))
                .build();
        mSettingsActivityComponent.inject(this);
    }
    // endregion

    // region Helper methods UI
    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setupScreen(int screen) {
        switch (SettingsScreen.values()[screen]) {
            case FACEBOOK_SETTINGS:
                mTitle = R.string.facebook_settings_title;
                setTitle(mTitle);
                replaceFragment(R.id.fragment_container, new FacebookSettingsFragment(), FacebookSettingsFragment.TAG);
                break;
            case LOCAL_CALENDAR_SETTINGS:
                mTitle = R.string.local_calendar_settings_title;
                setTitle(mTitle);
                replaceFragment(R.id.fragment_container, new LocalCalendarSettingsFragment(), LocalCalendarSettingsFragment.TAG);
                break;
            case SYNC_SETTINGS:
            default:
                mTitle = R.string.sync_settings_title;
                setTitle(mTitle);
                replaceFragment(R.id.fragment_container, new SyncSettingsFragment(), SyncSettingsFragment.TAG);
                break;
        }
    }

    // region Helper methods
    private void replaceFragment(int containerId, Fragment fragment, String tag) {
        if (getSupportFragmentManager() != null) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(containerId, fragment, tag);
            fragmentTransaction.commit();
        }
    }
    // endregion
}
