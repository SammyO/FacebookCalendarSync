package com.oddhov.facebookcalendarsync;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.oddhov.facebookcalendarsync.data.Constants;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,
        FacebookCallback<LoginResult> {
    //region Fields
    private LoginButton loginButton;
    private TextView tvSync;
    private Button btnSyncAll;
    private Button btnSyncUpcoming;
    private Button btnRetrieveToken;

    private CallbackManager mCallbackManager;
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
        verifyAccessTokenExpired();
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
        if (mCallbackManager.onActivityResult(requestCode, resultCode, data)) {
            return;
        }
    }
    //endregion

    //region onClickListeners
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnRetrieveToken) {
            String token = retrieveTokenFromAuthManager();
            Log.e("MainActivity", "Facebook access token: " + token);
        }
    }
    //endregion

    //region callbacks
    @Override
    public void onSuccess(LoginResult loginResult) {
        AccessToken accessToken = loginResult.getAccessToken();
        if (accessToken != null) {
            updateAuthManager(accessToken);
        } else {
            // TODO display error to user
            LoginManager.getInstance().logOut();
            removeFromAuthManager();
            updateUI(false);
        }
    }

    @Override
    public void onCancel() {

    }

    @Override
    public void onError(FacebookException error) {

    }
    //endregion

    //region View Helper Methods
    private void setupViews() {
        setContentView(R.layout.activity_main);
        tvSync = (TextView) findViewById(R.id.tvSync);
        btnSyncAll = (Button) findViewById(R.id.btnSynAll);
        btnSyncUpcoming = (Button) findViewById(R.id.btnSyncUpcoming);
        btnRetrieveToken = (Button) findViewById(R.id.btnRetrieveToken);
        btnSyncAll.setOnClickListener(this);
        btnSyncUpcoming.setOnClickListener(this);
        btnRetrieveToken.setOnClickListener(this);
    }

    private void setupFacebook() {
        mCallbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.registerCallback(mCallbackManager, this);

        mAccessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken,
                                                       AccessToken currentAccessToken) {
                updateFacebookAccessToken(currentAccessToken);
            }
        };
        verifyAccessTokenExpired();
    }

    private void updateUI(boolean isLoggedIn) {
        if (isLoggedIn) {
            tvSync.setVisibility(TextView.VISIBLE);
            btnSyncAll.setVisibility(View.VISIBLE);
            btnSyncUpcoming.setVisibility(View.VISIBLE);
        } else {
            tvSync.setVisibility(TextView.INVISIBLE);
            btnSyncAll.setVisibility(View.INVISIBLE);
            btnSyncUpcoming.setVisibility(View.INVISIBLE);
        }
    }
    //endregion

    // region AccessToken Helper Methods
    private void updateFacebookAccessToken(AccessToken accessToken) {
        if (accessToken == null) {
            LoginManager.getInstance().logOut();
            updateUI(false);
            removeFromAuthManager();
        } else {
            updateUI(true);
            updateAuthManager(accessToken);
        }
    }

    private void verifyAccessTokenExpired() {
        if (AccessToken.getCurrentAccessToken() != null && AccessToken.getCurrentAccessToken().isExpired()) {
            LoginManager.getInstance().logOut();
            updateUI(false);
            removeFromAuthManager();
        } else {
            updateUI(true);
        }
    }
    //endregion

    //region AuthenticationManager Helper Methods
    private void updateAuthManager(AccessToken accessToken) {
        AccountManager accountManager = (AccountManager) getSystemService(ACCOUNT_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Account[] accounts = accountManager.getAccountsByType(Constants.ACCOUNT_TYPE);
        Account account;
        if (accounts.length == 0) {
            account = new Account(Constants.ACCOUNT_NAME, Constants.ACCOUNT_TYPE);
            accountManager.addAccountExplicitly(account, accessToken.getToken(), null);
        } else {
            account = new Account(Constants.ACCOUNT_NAME, Constants.ACCOUNT_TYPE);
            accountManager.setPassword(account, accessToken.getToken());
        }
    }

    private String retrieveTokenFromAuthManager() {
        AccountManager accountManager = (AccountManager) getSystemService(ACCOUNT_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return null;
        }
        Account[] accounts = accountManager.getAccountsByType(Constants.ACCOUNT_TYPE);
        Account account;
        if (accounts.length != 0) {
            account = new Account(Constants.ACCOUNT_NAME, Constants.ACCOUNT_TYPE);
            return accountManager.getPassword(account);
        }
        return null;
    }

    private void removeFromAuthManager() {
        AccountManager accountManager = (AccountManager) getSystemService(ACCOUNT_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Account[] accounts = accountManager.getAccountsByType(Constants.ACCOUNT_TYPE);
        Account account;
        if (accounts.length != 0) {
            account = new Account(Constants.ACCOUNT_NAME, Constants.ACCOUNT_TYPE);
            accountManager.setPassword(account, null);
        }
    }
    //endregion
}
