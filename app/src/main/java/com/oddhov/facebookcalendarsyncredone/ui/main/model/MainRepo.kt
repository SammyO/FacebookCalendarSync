package com.oddhov.facebookcalendarsyncredone.ui.main.model

import com.facebook.AccessToken
import com.oddhov.facebookcalendarsyncredone.ui.main.MainContract
import javax.inject.Inject

/**
 * Created by sammy on 06/12/2017.
 */
class MainRepo
@Inject
constructor(): MainContract.Repo {
override fun hasEmptyOrExpiredAccessToken(): Boolean {
    val accessToken = AccessToken.getCurrentAccessToken()
    return (accessToken == null || accessToken.isExpired)
}
}