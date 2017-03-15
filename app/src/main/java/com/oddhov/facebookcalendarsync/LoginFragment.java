package com.oddhov.facebookcalendarsync;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.oddhov.facebookcalendarsync.utils.AccountManagerUtils;

import java.util.Arrays;

public class LoginFragment extends Fragment implements View.OnClickListener, FacebookCallback<LoginResult> {

    public static final String TAG = "LoginFragment";

    private Button btnLoginFacebook;
    private CallbackManager mCallbackManager;
    private AccountManagerUtils mAccountManagerUtils;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.login_fragment, container, false);
        btnLoginFacebook = (Button) view.findViewById(R.id.btnLoginFacebook);
        btnLoginFacebook.setOnClickListener(this);

        mAccountManagerUtils = new AccountManagerUtils(getActivity());

        setupFacebook();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    // region OnClickListeners
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnLoginFacebook:
                LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email", "public_profile", "user_events"));
                break;
        }
    }
    // endregion

    // region Facebook Listener methods
    @Override
    public void onSuccess(LoginResult loginResult) {
        mAccountManagerUtils.updateAuthManager(loginResult.getAccessToken());
        navigate();
    }

    @Override
    public void onCancel() {
        Toast toast = Toast.makeText(getActivity(), R.string.login_again, Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    public void onError(FacebookException error) {
        Toast toast = Toast.makeText(getActivity(), R.string.login_again, Toast.LENGTH_SHORT);
        toast.show();
    }
    // endregion

    // region Helper methods UI
    private void setupFacebook() {
        mCallbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(mCallbackManager, this);
    }
    // endregion

    // region Helper methods UI
    // TODO remove this and the next when EventBus is here
    private void navigate() {
        if (needsPermissions()) {
            replaceFragment(R.id.fragment_container, new PermissionsFragment(), PermissionsFragment.TAG);
        } else if (hasEmptyOrExpiredAccessToken()) {
            replaceFragment(R.id.fragment_container, new LoginFragment(), LoginFragment.TAG);
        } else {
            replaceFragment(R.id.fragment_container, new SyncFragment(), SyncFragment.TAG);
        }
    }

    private void replaceFragment(int containerId, Fragment fragment, String tag) {
        if (getFragmentManager() != null) {
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.replace(containerId, fragment, tag);
            fragmentTransaction.commit();
        }
    }

    private boolean needsPermissions() {
        int readCalendarPermission = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CALENDAR);
        int writeCalendarPermission = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_CALENDAR);
        int accountsPermission = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.GET_ACCOUNTS);

        if (readCalendarPermission == PackageManager.PERMISSION_GRANTED &&
                writeCalendarPermission == PackageManager.PERMISSION_GRANTED &&
                accountsPermission == PackageManager.PERMISSION_GRANTED) {
            return false;
        } else {
            return true;
        }
    }

    private boolean hasEmptyOrExpiredAccessToken() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken == null || accessToken.isExpired();
    }
    //endregion
}
