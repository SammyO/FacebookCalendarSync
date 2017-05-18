package com.oddhov.facebookcalendarsync.app;

import android.support.v7.app.AppCompatActivity;

import com.oddhov.facebookcalendarsync.data.dagger.PerActivity;

import dagger.Module;
import dagger.Provides;

@Module
public class ActivityModule {
    protected AppCompatActivity mActivity;

    public ActivityModule(AppCompatActivity activity) {
        this.mActivity = activity;
    }

    @Provides
    @PerActivity
    AppCompatActivity getActivity() {
        return mActivity;
    }
}
