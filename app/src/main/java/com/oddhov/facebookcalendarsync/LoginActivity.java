package com.oddhov.facebookcalendarsync;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

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
import com.oddhov.facebookcalendarsync.utils.AccountManagerUtils;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, FacebookCallback<LoginResult> {
    // region Fields
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
        if (mCallbackManager.onActivityResult(requestCode, resultCode, data)) {
            return;
        }
    }
    //endregion

    //region OnClickListeners
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnRetrieveToken) {
            String token = AccountManagerUtils.retrieveTokenFromAuthManager(this);
            Log.e("MainActivity", "Facebook access token: " + token);
        }
    }
    //endregion

    //region Facebook Listener methods
    @Override
    public void onSuccess(LoginResult loginResult) {
        if (verifyAccessTokenPresentAndValid()) {
            AccountManagerUtils.updateAuthManager(this, loginResult.getAccessToken());
            startMainActivity();
        } else {
            Toast toast = Toast.makeText(this, R.string.login_again, Toast.LENGTH_SHORT);
            toast.show();
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
        setContentView(R.layout.activity_login);

        btnRetrieveToken = (Button) findViewById(R.id.btnRetrieveToken);
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
                if (verifyAccessTokenPresentAndValid()) {
                    AccountManagerUtils.updateAuthManager(LoginActivity.this, currentAccessToken);
                }
            }
        };
        verifyAccessTokenPresentAndValid();
    }

    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
    //endregion

    // region AccessToken Helper Methods

    //endregion

    // region AccessToken Helper Methods
    private boolean verifyAccessTokenPresentAndValid() {
        if (AccessToken.getCurrentAccessToken() == null || AccessToken.getCurrentAccessToken().isExpired()) {
            LoginManager.getInstance().logOut();
            // TODO add permissions check
            AccountManagerUtils.removeFromAuthManager(this);
            return false;
        }
        return true;
    }
    //endregion
}
