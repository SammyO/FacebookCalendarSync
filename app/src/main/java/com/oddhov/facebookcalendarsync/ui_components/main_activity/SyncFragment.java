package com.oddhov.facebookcalendarsync.ui_components.main_activity;

import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.oddhov.facebookcalendarsync.R;
import com.oddhov.facebookcalendarsync.data.events.NavigateEvent;
import com.oddhov.facebookcalendarsync.data.events.SyncAdapterRanEvent;
import com.oddhov.facebookcalendarsync.syncadapter.SyncAdapterRanReceiver;
import com.oddhov.facebookcalendarsync.utils.AccountUtils;
import com.oddhov.facebookcalendarsync.utils.DatabaseUtils;
import com.oddhov.facebookcalendarsync.utils.PermissionUtils;
import com.oddhov.facebookcalendarsync.utils.SyncAdapterUtils;
import com.oddhov.facebookcalendarsync.utils.TimeUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class SyncFragment extends Fragment {
    //region static fields
    public static final String TAG = "SyncFragment";
    //endregion

    //region fields
    @Inject
    PermissionUtils mPermissionUtils;
    @Inject
    DatabaseUtils mDatabaseUtils;
    @Inject
    TimeUtils mTimeUtils;
    @Inject
    SyncAdapterUtils mSyncAdapterUtils;

    private SyncAdapterRanReceiver mSyncAdapterRanReceiver;
    private Unbinder mUnbinder;
    //endregion

    //region VI
    @BindView(R.id.tvLastSyncedValue)
    TextView tvLastSynced;
    //endregion

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sync, container, false);
        mUnbinder = ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initialize();
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
        if (!hasNetworkConnection()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(R.string.no_network_title);
            builder.setMessage(R.string.no_network_message);
            builder.setPositiveButton(android.R.string.ok, null);
            builder.setCancelable(true);
            builder.show();
        } else if (mDatabaseUtils.getSyncAdapterPaused()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(R.string.sync_paused_title);
                builder.setMessage(R.string.sync_paused_message);
                builder.setPositiveButton(android.R.string.ok, null);
                builder.setCancelable(true);
                builder.show();
        } else {
            mSyncAdapterUtils.runSyncAdapterNow();
        }
    }
    // endregion

    // region EventBus methods
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSyncAdapterRunEvent(SyncAdapterRanEvent event) {
        tvLastSynced.setText(mTimeUtils.convertEpochFormatToDate(mDatabaseUtils.getLastSynced()));
    }

    // region Helper Methods Dagger
    private void initialize() {
        ((MainActivity) getActivity()).getComponent().inject(this);

        mSyncAdapterRanReceiver = new SyncAdapterRanReceiver();
        getActivity().registerReceiver(mSyncAdapterRanReceiver, new IntentFilter("com.oddhov.facebookcalendarsync"));

        if (mDatabaseUtils.getLastSynced() == 0) {
            tvLastSynced.setText(R.string.not_synced_yet);
        } else {
            tvLastSynced.setText(mTimeUtils.convertEpochFormatToDate(mDatabaseUtils.getLastSynced()));
        }
    }

    private boolean hasNetworkConnection() {
        if (mPermissionUtils.needsPermissions()) {
            EventBus.getDefault().post(new NavigateEvent());
        }

        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null;

    }
    // endregion
}
