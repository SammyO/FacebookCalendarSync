package com.oddhov.facebookcalendarsync.ui_components.settings_activity.base;

import android.app.Dialog;
import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;

import com.oddhov.facebookcalendarsync.R;
import com.oddhov.facebookcalendarsync.data.models.ActivityTransition;
import com.oddhov.facebookcalendarsync.utils.SyncAdapterUtils;

import javax.inject.Inject;

public class SettingsBaseFragment extends Fragment implements Dialog.OnClickListener {
    // region Fields
    @Inject
    SyncAdapterUtils mSyncAdapterUtils;
    // endregion

    // region Dialog.OnClickListener interface
    @Override
    public void onClick(DialogInterface dialogInterface, int which) {
        if (which == DialogInterface.BUTTON_POSITIVE) {
            mSyncAdapterUtils.runSyncAdapterNow();
        }
        navigateBack();
    }
    // endregion

    // region Helper methods (UI)
    protected void showSettingsChangedDialog() {
        new AlertDialog.Builder(getActivity())
                .setTitle(R.string.dialog_settings_changed_title)
                .setMessage(R.string.dialog_settings_changed_description)
                .setPositiveButton(android.R.string.yes, this)
                .setNegativeButton(android.R.string.no, this)
                .show();
    }
    // endregion

    // region Helper methods (navigation)
    protected void navigateBack() {
        getActivity().finish();
        ActivityTransition transition = ActivityTransition.BACK;
        getActivity().overridePendingTransition(transition.getEnter(), transition.getExit());
    }
    // endregion
}
