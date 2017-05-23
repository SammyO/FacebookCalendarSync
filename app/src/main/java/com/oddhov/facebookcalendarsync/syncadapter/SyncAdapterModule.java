package com.oddhov.facebookcalendarsync.syncadapter;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class SyncAdapterModule {
    private Context mContext;

    public SyncAdapterModule(Context context) {
        mContext = context;
    }

    @Provides
    @Singleton
    Context provideContext() {
        return this.mContext;
    }
}
