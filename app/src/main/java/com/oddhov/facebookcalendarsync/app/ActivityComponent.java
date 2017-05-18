package com.oddhov.facebookcalendarsync.app;

import android.support.v7.app.AppCompatActivity;

import com.oddhov.facebookcalendarsync.data.dagger.PerActivity;

import dagger.Component;

@PerActivity
@Component(dependencies = {ApplicationComponent.class}, modules = {ActivityModule.class})
public interface ActivityComponent {
    AppCompatActivity getActivity();
}
