package com.oddhov.facebookcalendarsync;

import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.oddhov.facebookcalendarsync.data.events.NavigateEvent;
import com.oddhov.facebookcalendarsync.data.events.SyncAdapterRanEvent;
import com.oddhov.facebookcalendarsync.data.exceptions.RealmException;
import com.oddhov.facebookcalendarsync.utils.AccountUtils;
import com.oddhov.facebookcalendarsync.utils.DatabaseUtils;
import com.oddhov.facebookcalendarsync.utils.PermissionUtils;
import com.oddhov.facebookcalendarsync.utils.SyncAdapterUtils;
import com.oddhov.facebookcalendarsync.utils.TimeUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class SyncFragment extends Fragment {
    //region static fields
    public static final String TAG = "SyncFragment";
    //endregion
    //region VI
    @BindView(R.id.tvLastSyncedValue)
    TextView tvLastSynced;
    //region fields
    private PermissionUtils mPermissionUtils;
    private DatabaseUtils mDatabaseUtils;
    private TimeUtils mTimeUtils;
    private SyncAdapterUtils mSyncAdapterUtils;
    private SyncAdapterRanReceiver mSyncAdapterRanReceiver;
    //endregion
    private Unbinder mUnbinder;
    //endregion

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSyncAdapterRanReceiver = new SyncAdapterRanReceiver();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mPermissionUtils = new PermissionUtils(getActivity());
        mDatabaseUtils = new DatabaseUtils(getActivity());
        mTimeUtils = new TimeUtils();
        mSyncAdapterUtils = new SyncAdapterUtils();

        getActivity().registerReceiver(mSyncAdapterRanReceiver, new IntentFilter("com.oddhov.facebookcalendarsync"));
        View view = inflater.inflate(R.layout.sync_fragment, container, false);
        mUnbinder = ButterKnife.bind(this, view);

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
        mUnbinder.unbind();
        super.onDestroyView();
    }
    // endregion

    // region VI methods
    @OnClick(R.id.btnSynNow)
    public void onSyncNowClicked() {
        mSyncAdapterUtils.runSyncAdapter();
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
