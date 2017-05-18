package com.oddhov.facebookcalendarsync.app;

import android.content.Context;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class ApplicationModule {
    private Context mAppContext;
    private EventBus mEventBus;

    public ApplicationModule(Context appContext, EventBus eventBus) {
        mAppContext = appContext;
        mEventBus = eventBus;
    }

    @Provides
    @Singleton
    Context provideAppContext() {
        return this.mAppContext;
    }

    @Provides
    @Singleton
    EventBus provideEventBus() {
        return mEventBus;
    }
}
