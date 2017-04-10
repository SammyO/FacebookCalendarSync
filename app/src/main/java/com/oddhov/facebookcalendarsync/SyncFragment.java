package com.oddhov.facebookcalendarsync;

import android.accounts.Account;
import android.content.ContentResolver;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.oddhov.facebookcalendarsync.data.Constants;

public class SyncFragment extends Fragment implements View.OnClickListener {

    public static final String TAG = "SyncFragment";

    private Button btnSyncNow;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.sync_fragment, container, false);

        btnSyncNow = (Button) view.findViewById(R.id.btnSynNow);
        btnSyncNow.setOnClickListener(this);

        return view;
    }

    // region OnClickListeners
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnSynNow) {
            startSyncAdapter();
        }
    }
    // endregion

    // region Helper methods
    private void startSyncAdapter() {
        Account account = new Account(Constants.ACCOUNT_NAME, Constants.ACCOUNT_TYPE);
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.setIsSyncable(account, "com.android.calendar", 1);
        ContentResolver.addPeriodicSync(account, "com.android.calendar", Bundle.EMPTY, Constants.SYNC_INTERVAL);
        ContentResolver.setSyncAutomatically(account, "com.android.calendar", true);
        ContentResolver.requestSync(account, "com.android.calendar", bundle);
    }
    // endregion
}
