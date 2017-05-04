package com.oddhov.facebookcalendarsync;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.internal.CallbackManagerImpl;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.oddhov.facebookcalendarsync.utils.AccountUtils;

import java.util.Arrays;

public class LoginFragment extends Fragment implements View.OnClickListener, FacebookCallback<LoginResult> {

    public static final String TAG = "LoginFragment";

    private Button btnLoginFacebook;
    private CallbackManager mCallbackManager;
    private NavigationListener mNavigationListenerCallback;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.login_fragment, container, false);
        btnLoginFacebook = (Button) view.findViewById(R.id.btnLoginFacebook);
        btnLoginFacebook.setOnClickListener(this);

        setupFacebook();

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mNavigationListenerCallback = (NavigationListener) context;
        } catch (ClassCastException e) {
            Log.e(TAG, context.toString()
                    + " must implement LoginNavigationListener");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!AccountUtils.hasEmptyOrExpiredAccessToken()) {
            mNavigationListenerCallback.navigate();
        }
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        if (requestCode == CallbackManagerImpl.RequestCodeOffset.Login.toRequestCode()) {
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        }
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
        mNavigationListenerCallback.navigate();
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
}
