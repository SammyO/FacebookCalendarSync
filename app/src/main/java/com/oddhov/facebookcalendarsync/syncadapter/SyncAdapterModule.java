package com.oddhov.facebookcalendarsync.syncadapter;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class SyncAdapterModule {
    private Context mAppContext;

    public SyncAdapterModule(Context appContext) {
        mAppContext = appContext;
    }

    @Provides
    @Singleton
    Context provideAppContext() {
        return this.mAppContext;
    }
}
