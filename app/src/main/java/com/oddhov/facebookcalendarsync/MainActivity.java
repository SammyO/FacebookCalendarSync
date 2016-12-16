package com.oddhov.facebookcalendarsync;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.oddhov.facebookcalendarsync.data.Constants;
import com.oddhov.facebookcalendarsync.utils.AccountManagerUtils;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    //region Fields
    private Button btnLogOut;
    private Button btnSyncAll;
    private Button btnSyncUpcoming;
    private Button btnRetrieveToken;

    private AccessTokenTracker mAccessTokenTracker;
    //endregion

    //region Lifecycle Methods
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_main);

        setupViews();
        setupFacebook();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("MainActivity", "onResume()");
        verifyAccessTokenPresentAndValid();
        mAccessTokenTracker.startTracking();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mAccessTokenTracker.stopTracking();
    }
    //endregion

    //region Activity Methods
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.LOGIN_ACTIVITY_REQUEST) {
            Log.e("MainActivity", "onActivityResult received");
            // Check if the token is really valid, and if not, return back to LoginActivity
            verifyAccessTokenPresentAndValid();
        }
    }
    //endregion

    //region onClickListeners
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnRetrieveToken:
                // TODO add permission check
                String token = AccountManagerUtils.retrieveTokenFromAuthManager(this);
                Log.e("MainActivity", "Facebook access token: " + token);
                break;
            case R.id.btnLogOut:
                // TODO move this to settings
                // TODO proceed with "Are you sure?" alert
                setLoginStatusToLoggedOut();
                break;
        }
    }
    //endregion

    // region AccessToken Helper Methods
    private boolean verifyAccessTokenPresentAndValid() {
        if (AccessToken.getCurrentAccessToken() == null || AccessToken.getCurrentAccessToken().isExpired()) {
            setLoginStatusToLoggedOut();
            return false;
        }
        return true;
    }
    //endregion

    //region Helper methods
    private void setLoginStatusToLoggedOut() {
        LoginManager.getInstance().logOut();
        // TODO add permissions check
        AccountManagerUtils.removeFromAuthManager(this);
        startLoginActivity();
    }

    private void setupFacebook() {
        mAccessTokenTracker = new AccessTokenTracker() {
            @Override
            protected final void onCurrentAccessTokenChanged(AccessToken oldAccessToken,
                                                       AccessToken currentAccessToken) {
                if (verifyAccessTokenPresentAndValid()) {
                    Log.e("MainActivity", "FonCurrentAccessTokenChanged");
                    AccountManagerUtils.updateAuthManager(MainActivity.this, currentAccessToken);
                }
            }
        };
    }

    private void setupViews() {
        btnLogOut = (Button) findViewById(R.id.btnLogOut);
        btnLogOut.setOnClickListener(this);

        btnSyncAll = (Button) findViewById(R.id.btnSynAll);
        btnSyncUpcoming = (Button) findViewById(R.id.btnSyncUpcoming);
        btnRetrieveToken = (Button) findViewById(R.id.btnRetrieveToken);
        btnSyncAll.setOnClickListener(this);
        btnSyncUpcoming.setOnClickListener(this);
        btnRetrieveToken.setOnClickListener(this);
    }

    private void startLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
    //endregion
}
