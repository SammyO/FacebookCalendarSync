package com.oddhov.facebookcalendarsync.ui_components.settings_activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.crashlytics.android.Crashlytics;
import com.oddhov.facebookcalendarsync.R;
import com.oddhov.facebookcalendarsync.data.exceptions.RealmException;
import com.oddhov.facebookcalendarsync.data.models.SyncRange;
import com.oddhov.facebookcalendarsync.utils.DatabaseUtils;
import com.oddhov.facebookcalendarsync.utils.SyncAdapterUtils;

public class FacebookSettingsFragment extends Fragment implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {

    // region Fields

    public static final String TAG = "FacebookSettingsFragment";

    private DatabaseUtils mDatabaseUtils;
    private SyncAdapterUtils mSyncAdapterUtils;

    private SwitchCompat swSyncBirthdays;
    private SwitchCompat swShowLinks;
    private RadioGroup rgSyncRange;
    private RadioButton rbSynAll;
    private RadioButton rbSynUpcoming;

    // endregion

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mDatabaseUtils = new DatabaseUtils(getActivity());
        mSyncAdapterUtils = new SyncAdapterUtils();

        View view = inflater.inflate(R.layout.fragment_facebook_settings, container, false);
        setupViews(view);
        return view;
    }

    // region Interface View.OnClickListener
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.swSyncBirthdays:
                try {
                    mDatabaseUtils.setSyncBirthdays(swSyncBirthdays.isChecked());
                } catch (RealmException e) {
                    Crashlytics.logException(e);
                }
                break;
            case R.id.swShowLinks:
                try {
                    mDatabaseUtils.setShowLinks(swShowLinks.isChecked());
                } catch (RealmException e) {
                    Crashlytics.logException(e);
                }
                break;
            default:
                break;
        }
    }
    // endregion

    // region Interface RadioGroup.OnCheckedChangeListener
    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int which) {
        Log.e("FacebookSettings", "onCheckedChanged");

        switch (which) {
            case R.id.rbSyncRangeAll:
                try {
                    mDatabaseUtils.setSyncRange(SyncRange.SYNC_ALL.ordinal());
                } catch (RealmException e) {
                    Crashlytics.logException(e);
                }
                break;
            case R.id.rbSyncRangeUpcoming:
                try {
                    mDatabaseUtils.setSyncRange(SyncRange.SYNC_UPCOMING.ordinal());
                } catch (RealmException e) {
                    Crashlytics.logException(e);
                }
                break;
            default:
                break;
        }
    }
    // endregion

    // region Helper Methods (UI)
    private void setupViews(View view) {
        try {
            swSyncBirthdays = (SwitchCompat) view.findViewById(R.id.swSyncBirthdays);
            swSyncBirthdays.setChecked(mDatabaseUtils.getSyncBirthdays());
            swSyncBirthdays.setOnClickListener(this);

            swShowLinks = (SwitchCompat) view.findViewById(R.id.swShowLinks);
            swShowLinks.setChecked(mDatabaseUtils.getShowLinks());
            swShowLinks.setOnClickListener(this);

            rbSynAll = (RadioButton) view.findViewById(R.id.rbSyncRangeAll);
            rbSynUpcoming = (RadioButton) view.findViewById(R.id.rbSyncRangeUpcoming);

            rgSyncRange = (RadioGroup) view.findViewById(R.id.rgSyncRange);
            rgSyncRange.setOnCheckedChangeListener(this);
            switch (SyncRange.values()[mDatabaseUtils.getSyncRange()]) {
                case SYNC_ALL:
                    rbSynAll.setChecked(true);
                    break;
                case SYNC_UPCOMING:
                    rbSynUpcoming.setChecked(true);
                    break;
                default:
                    break;
            }

        } catch (RealmException e) {
            e.printStackTrace();
        }
    }
    // endregion
}
