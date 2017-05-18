package com.oddhov.facebookcalendarsync.ui_components.settings_activity;

import com.oddhov.facebookcalendarsync.app.ActivityComponent;
import com.oddhov.facebookcalendarsync.app.ActivityModule;
import com.oddhov.facebookcalendarsync.app.ApplicationComponent;
import com.oddhov.facebookcalendarsync.data.dagger.PerActivity;

import dagger.Component;

@PerActivity
@Component(dependencies = {ApplicationComponent.class},
        modules = {ActivityModule.class})

public interface SettingsActivityComponent extends ActivityComponent {
    void inject(SettingsActivity settingsActivity);

    void inject(SyncSettingsFragment syncSettingsFragment);

    void inject(FacebookSettingsFragment facebookSettingsFragment);

    void inject(LocalCalendarSettingsFragment localCalendarSettingsFragment);
}
