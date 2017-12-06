package com.oddhov.facebookcalendarsync.ui_components.main_activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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
import com.mikepenz.materialdrawer.holder.StringHolder;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.oddhov.facebookcalendarsync.R;
import com.oddhov.facebookcalendarsync.app.ActivityModule;
import com.oddhov.facebookcalendarsync.app.FacebookCalendarSyncApplication;
import com.oddhov.facebookcalendarsync.data.Constants;
import com.oddhov.facebookcalendarsync.data.events.NavigateEvent;
import com.oddhov.facebookcalendarsync.utils.AccountUtils;
import com.oddhov.facebookcalendarsync.utils.DatabaseUtils;
import com.oddhov.facebookcalendarsync.utils.NavigationDrawerUtils;
import com.oddhov.facebookcalendarsync.utils.PermissionUtils;
import com.oddhov.facebookcalendarsync.utils.SyncAdapterUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import javax.inject.Inject;

public class MainActivity extends AppCompatActivity implements DialogInterface.OnClickListener,
        Drawer.OnDrawerItemClickListener, FacebookCallback<LoginResult> {
    //region Fields
    @Inject
    PermissionUtils mPermissionUtils;
    @Inject
    SyncAdapterUtils mSyncAdapterUtils;
    @Inject
    DatabaseUtils mDatabaseUtils;
    @Inject
    NavigationDrawerUtils mNavigationDrawerUtils;

    private MainActivityComponent mMainActivityComponent;

    private Drawer mNavigationDrawer;
    private CallbackManager mCallbackManager;
    private Toolbar mToolbar;
    //endregion

    //region Lifecycle Methods
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initializeInjector();

        setContentView(R.layout.activity_main);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        mCallbackManager = CallbackManager.Factory.create();

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

    // region Activity methods
    @Override
    public void onBackPressed() {
        if (mNavigationDrawer != null && mNavigationDrawer.isDrawerOpen()) {
            mNavigationDrawer.closeDrawer();
        } else {
            super.onBackPressed();
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
                mNavigationDrawerUtils.onStartStopClicked(mNavigationDrawer);
                mNavigationDrawer.closeDrawer();
                return true;
            case Constants.LOG_IN_OUT:
                mNavigationDrawerUtils.onLoginLogoutClicked(mCallbackManager, this, this, this);
                mNavigationDrawer.closeDrawer();
                return true;
            case Constants.REPORT_BUG:

                return true;
            case Constants.FACEBOOK_SETTINGS:
                mNavigationDrawerUtils.onFacebookSettingsClicked(this);
                return true;
            case Constants.LOCAL_CALENDAR_SETTINGS:
                mNavigationDrawerUtils.onLocalCalendarSettingsClicked(this);
                return true;
            case Constants.SYNC_SETTINGS:
                mNavigationDrawerUtils.onSyncSettingsClicked(this);
                return true;
            default:
                return false;
        }
    }
    // endregion

    // region EventBus
    @Subscribe(threadMode = ThreadMode.ASYNC)
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

    // region Helper Methods Dagger
    public MainActivityComponent getComponent() {
        return this.mMainActivityComponent;
    }

    private void initializeInjector() {
//        mMainActivityComponent = DaggerMainActivityComponent.builder()
//                .applicationComponent(((FacebookCalendarSyncApplication) getApplication()).getApplicationComponent())
//                .activityModule(new ActivityModule(this))
//                .build();
//        mMainActivityComponent.inject(this);
    }
    // endregion

    // region Helper methods UI
    private void setupNavigationDrawer() {
        mNavigationDrawer = mNavigationDrawerUtils.buildNavigationDrawer(this, this, mToolbar);

        if (getSupportActionBar() != null) {
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

