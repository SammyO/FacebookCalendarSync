package com.oddhov.facebookcalendarsync;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.oddhov.facebookcalendarsync.data.Constants;
import com.oddhov.facebookcalendarsync.data.events.SyncAdapterRanEvent;

import org.greenrobot.eventbus.EventBus;

public class SyncAdapterRanReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getBooleanExtra(Constants.SYNC_ADAPTER_RAN_EXTRA, false)) {
            EventBus.getDefault().post(new SyncAdapterRanEvent());
        }
    }
}
