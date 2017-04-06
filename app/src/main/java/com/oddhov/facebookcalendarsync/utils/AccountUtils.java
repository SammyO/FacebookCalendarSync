package com.oddhov.facebookcalendarsync.utils;

import com.facebook.AccessToken;

public class AccountUtils {

    public static boolean hasEmptyOrExpiredAccessToken() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if (accessToken == null || accessToken.isExpired()) {
            return true;
        }
        return false;
    }
}
