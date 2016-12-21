package com.oddhov.facebookcalendarsync;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.oddhov.facebookcalendarsync.data.Constants;
import com.oddhov.facebookcalendarsync.utils.AccountManagerUtils;

import java.util.Arrays;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener,
        DialogInterface.OnClickListener, FacebookCallback<LoginResult>, AccessToken.AccessTokenRefreshCallback {
    // region Fields
    private Button btnLoginFacebook;
    private Button btnRetrieveToken;

    private CallbackManager mCallbackManager;
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
        if (verifyAccessTokenNotPresentOrInvalid(AccessToken.getCurrentAccessToken())) {
            // If we don't have permissions to update the token, set the state to logged out
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {
                setStateToLoggedOut();
                return;
            }
            AccessToken.refreshCurrentAccessTokenAsync(this);
        } else {
            btnRetrieveToken.setVisibility(View.VISIBLE);
//            startMainActivity();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    //endregion

    //region Activity Methods
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        if (grantResults.length == 0) {
            return;
        }
        if (requestCode == Constants.REQUEST_ACCOUNTS_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.GET_ACCOUNTS)) {
                    showRequestPermissionRationale();
                }
            }
        }
    }
    //endregion

    //region OnClickListeners
    @Override
    public void onClick(View view) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.GET_ACCOUNTS},
                    Constants.REQUEST_ACCOUNTS_PERMISSION);
            return;
        }
        switch (view.getId()) {
            case R.id.btnRetrieveToken:
                String token = AccountManagerUtils.retrieveTokenFromAuthManager(this);
                Log.e("LoginActivity", "Facebook access token: " + token);
                break;
            case R.id.btnLoginFacebook:
                LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email", "public_profile", "user_events"));
                break;
        }
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        startSettingsActivity();
    }
    //endregion

    //region Facebook Listener methods
    @Override
    public void onSuccess(LoginResult loginResult) {
        updateAccountManagerToken(loginResult.getAccessToken());
    }

    @Override
    public void onCancel() {

    }

    @Override
    public void onError(FacebookException error) {

    }
    //endregion

    // region Facebook refreshToken callback methods
    @Override
    public void OnTokenRefreshed(AccessToken accessToken) {
        updateAccountManagerToken(accessToken);
    }

    @Override
    public void OnTokenRefreshFailed(FacebookException exception) {
        setStateToLoggedOut();
    }
    //endregion

    //region UI Helper methods
    private void showRequestPermissionRationale() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.request_permissions_title)
                .setMessage(R.string.request_accounts_permissions_description)
                .setPositiveButton(R.string.word_app_info, this)
                .show();
    }
    //endregion

    // region AccessToken Helper Methods
    private boolean verifyAccessTokenNotPresentOrInvalid(AccessToken accessToken) {
        return accessToken == null || accessToken.isExpired();
    }

    private void updateAccountManagerToken(AccessToken accessToken) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {
            setStateToLoggedOut();
            return;
        }
        AccountManagerUtils.updateAuthManager(LoginActivity.this, accessToken);
    }

    private void removeTokenFromAccountManagerIfPresent() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if (AccountManagerUtils.retrieveTokenFromAuthManager(this) != null) {
            AccountManagerUtils.removeFromAuthManager(this);
        }
    }
    //endregion

    //region View Helper Methods
    private void setStateToLoggedOut() {
        LoginManager.getInstance().logOut();
        removeTokenFromAccountManagerIfPresent();
        btnRetrieveToken.setVisibility(View.GONE);
        Toast toast = Toast.makeText(this, R.string.login_again, Toast.LENGTH_SHORT);
        toast.show();
    }

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
    }

    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    private void startSettingsActivity() {
        Intent myAppSettings = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.parse("package:" + getPackageName()));
        myAppSettings.addCategory(Intent.CATEGORY_DEFAULT);
        myAppSettings.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivityForResult(myAppSettings, Constants.REQUEST_APP_SETTINGS);
    }
    //endregion
}
