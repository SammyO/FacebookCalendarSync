package com.oddhov.facebookcalendarsync.ui_components.main_activity;

import com.oddhov.facebookcalendarsync.app.ActivityComponent;
import com.oddhov.facebookcalendarsync.app.ActivityModule;
import com.oddhov.facebookcalendarsync.app.ApplicationComponent;
import com.oddhov.facebookcalendarsync.data.dagger.PerActivity;

import dagger.Component;

@PerActivity
@Component(dependencies = {ApplicationComponent.class},
        modules = {ActivityModule.class})
public interface MainActivityComponent extends ActivityComponent {
    void inject(MainActivity mainActivity);

    void inject(LoginFragment loginFragment);

    void inject(PermissionsFragment permissionsFragment);

    void inject(SyncFragment syncFragment);
}
