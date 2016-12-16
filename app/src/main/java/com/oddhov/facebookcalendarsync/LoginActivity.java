package com.oddhov.facebookcalendarsync;

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
import com.oddhov.facebookcalendarsync.utils.AccountManagerUtils;

import java.util.Arrays;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, FacebookCallback<LoginResult> {
    // region Fields
    private Button btnLoginFacebook;
    private Button btnRetrieveToken;

    private CallbackManager mCallbackManager;
    private AccessTokenTracker mAccessTokenTracker;
    //endregion

    //region Lifecycle Methods
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("LoginActivity", "onCreate()");
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
        switch (view.getId()) {
            case R.id.btnRetrieveToken:
                // TODO add permission check
                String token = AccountManagerUtils.retrieveTokenFromAuthManager(this);
                Log.e("LoginActivity", "Facebook access token: " + token);
                break;
            case R.id.btnLoginFacebook:
                LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email", "public_profile", "user_events"));
                break;
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

    //region View Helper Methods
    private void setupViews() {
        setContentView(R.layout.activity_login);

        btnLoginFacebook = (Button) findViewById(R.id.btnLoginFacebook);
        btnLoginFacebook.setOnClickListener(this);

        btnRetrieveToken = (Button) findViewById(R.id.btnRetrieveToken);
        btnRetrieveToken.setOnClickListener(this);
    }

    private void setupFacebook() {
        mCallbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(mCallbackManager, this);

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
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
    //endregion
}
