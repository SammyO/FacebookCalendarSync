package com.oddhov.facebookcalendarsync;


import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener, DialogInterface.OnClickListener {

    private TextView tvSyncEventsValue;
    private Button btnChangeSyncPreference;


    public static void start(Activity activity) {
        Intent intent = new Intent(activity, SettingsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.startActivity(intent);
    }

    //region Lifecycle Methods
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);


        setupViews();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return false;
    }
    //endregion

    // region Interface View.OnClickListener
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnSyncEvents) {
            CharSequence colors[] = new CharSequence[] {getString(R.string.sync_all_description),
                                                    getString(R.string.sync_upcoming_description)};

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.sync_preference_dialog_title);
            builder.setItems(colors, this);
            builder.show();
        }
    }
    // endregion

    // region interface DialogInterface.OnClickListener
    @Override
    public void onClick(DialogInterface dialogInterface, int option) {
        switch (option) {
            case 0:
                break;
            case 1:
                break;
        }
        setSyncModeValue();
    }
    // endregion

    // region Helper methods UI
    private void setupViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(R.string.settings_title);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        btnChangeSyncPreference = (Button) findViewById(R.id.btnSyncEvents);
        btnChangeSyncPreference.setOnClickListener(this);
        tvSyncEventsValue = (TextView) findViewById(R.id.tvSyncEventsValue);
        setSyncModeValue();
    }

    private void setSyncModeValue() {

    }
}
