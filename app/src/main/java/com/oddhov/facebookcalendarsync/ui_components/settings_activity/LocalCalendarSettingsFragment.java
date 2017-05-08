package com.oddhov.facebookcalendarsync.ui_components.settings_activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.oddhov.facebookcalendarsync.R;

public class LocalCalendarSettingsFragment extends Fragment {

    public static final String TAG = "LocalCalendarSettingsFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_local_calendar_settings, container, false);

        return view;
    }
}
