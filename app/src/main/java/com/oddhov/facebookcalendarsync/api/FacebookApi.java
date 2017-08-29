package com.oddhov.facebookcalendarsync.api;

import com.oddhov.facebookcalendarsync.data.models.EventsResponse;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface FacebookApi {
    /**
     * API variables
     */
    String BASE_URL = "https://graph.facebook.com";

    @GET("/me?fields=events")
    Single<EventsResponse> getEvents(
            @Query("access_token") String accessToken
    );
}
