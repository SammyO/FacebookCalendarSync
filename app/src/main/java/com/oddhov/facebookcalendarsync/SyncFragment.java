package com.oddhov.facebookcalendarsync;

import android.accounts.Account;
import android.content.ContentResolver;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.oddhov.facebookcalendarsync.data.Constants;

public class SyncFragment extends Fragment implements View.OnClickListener {

    public static final String TAG = "SyncFragment";

    private Button btnSyncAll;
    private Button btnSyncUpcoming;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.sync_fragment, container, false);

        btnSyncAll = (Button) view.findViewById(R.id.btnSynAll);
        btnSyncUpcoming = (Button) view.findViewById(R.id.btnSyncUpcoming);

        btnSyncAll.setOnClickListener(this);
        btnSyncUpcoming.setOnClickListener(this);

        return view;
    }

    // region OnClickListeners
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnSynAll:
                startSyncAdapter(false);
                break;
            case R.id.btnSyncUpcoming:
                startSyncAdapter(true);
                break;
        }
    }
    // endregion

    // region Helper methods
    private void startSyncAdapter(boolean onlyUpcomingEvents) {
        Account account = new Account(Constants.ACCOUNT_NAME, Constants.ACCOUNT_TYPE);
        Bundle bundle = new Bundle();
        bundle.putBoolean(Constants.SYNC_ONLY_UPCOMING_EVENTS, onlyUpcomingEvents);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.setIsSyncable(account, Constants.CALENDAR_AUTHORITY, 1);
        ContentResolver.addPeriodicSync(account, Constants.CALENDAR_AUTHORITY, Bundle.EMPTY, Constants.SYNC_INTERVAL);
        ContentResolver.setSyncAutomatically(account, Constants.CALENDAR_AUTHORITY, true);
        ContentResolver.requestSync(account, CalendarContract.AUTHORITY, bundle);
    }
    // endregion
}
