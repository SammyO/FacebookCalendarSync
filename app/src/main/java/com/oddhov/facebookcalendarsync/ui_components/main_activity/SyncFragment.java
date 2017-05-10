package com.oddhov.facebookcalendarsync.ui_components.main_activity;

import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.oddhov.facebookcalendarsync.R;
import com.oddhov.facebookcalendarsync.data.events.NavigateEvent;
import com.oddhov.facebookcalendarsync.data.events.SyncAdapterRanEvent;
import com.oddhov.facebookcalendarsync.data.exceptions.RealmException;
import com.oddhov.facebookcalendarsync.syncadapter.SyncAdapterRanReceiver;
import com.oddhov.facebookcalendarsync.utils.AccountUtils;
import com.oddhov.facebookcalendarsync.utils.DatabaseUtils;
import com.oddhov.facebookcalendarsync.utils.PermissionUtils;
import com.oddhov.facebookcalendarsync.utils.SyncAdapterUtils;
import com.oddhov.facebookcalendarsync.utils.TimeUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class SyncFragment extends Fragment implements View.OnClickListener {

    public static final String TAG = "SyncFragment";

    private PermissionUtils mPermissionUtils;
    private DatabaseUtils mDatabaseUtils;
    private TimeUtils mTimeUtils;
    private SyncAdapterUtils mSyncAdapterUtils;

    private SyncAdapterRanReceiver mSyncAdapterRanReceiver;

    private Button btnSyncNow;
    private TextView tvLastSynced;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSyncAdapterRanReceiver = new SyncAdapterRanReceiver();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sync, container, false);

        mPermissionUtils = new PermissionUtils(getActivity());
        mDatabaseUtils = new DatabaseUtils(getActivity());
        mTimeUtils = new TimeUtils();
        mSyncAdapterUtils = new SyncAdapterUtils();

        getActivity().registerReceiver(mSyncAdapterRanReceiver, new IntentFilter("com.oddhov.facebookcalendarsync"));

        btnSyncNow = (Button) view.findViewById(R.id.btnSynNow);
        btnSyncNow.setOnClickListener(this);

        tvLastSynced = (TextView) view.findViewById(R.id.tvLastSyncedValue);
        try {
            tvLastSynced.setText(mTimeUtils.convertEpochFormatToDate(mDatabaseUtils.getLastSynced()));
        } catch (RealmException e) {
            Crashlytics.logException(e);
        }

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (AccountUtils.hasEmptyOrExpiredAccessToken() || mPermissionUtils.needsPermissions()) {
            EventBus.getDefault().post(new NavigateEvent());
        }

    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        getActivity().unregisterReceiver(mSyncAdapterRanReceiver);
        super.onDestroyView();
    }
    // endregion

    // region OnClickListeners
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnSynNow) {
            mSyncAdapterUtils.runSyncAdapterNow();
        }
    }
    // endregion

    // region EventBus methods
    // TODO replace with BroadcastReceiver
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSyncAdapterRunEvent(SyncAdapterRanEvent event) {
        try {
            tvLastSynced.setText(mTimeUtils.convertEpochFormatToDate(mDatabaseUtils.getLastSynced()));
        } catch (RealmException e) {
            Crashlytics.logException(e);
        }

    }
    // endregion
}
