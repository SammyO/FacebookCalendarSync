package com.oddhov.facebookcalendarsync.api;


import android.util.Log;

import com.oddhov.facebookcalendarsync.utils.DatabaseUtils;

import java.util.concurrent.TimeUnit;

import io.reactivex.Completable;
import io.reactivex.Observable;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class FacebookService {
    private static final int DEFAULT_TIMEOUT = 10;
    private FacebookApi mFacebookApi;
    private DatabaseUtils mDatabaseUtils;

    public FacebookService(DatabaseUtils databaseUtils) {
        this.mDatabaseUtils = databaseUtils;
        createFacebookService();
    }

    private void createFacebookService() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .addInterceptor(interceptor)
                .build();

        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(FacebookApi.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(okHttpClient)
                .build();

        mFacebookApi = retrofit.create(FacebookApi.class);
    }

//    public void getEvents(String accessToken) {
//        Call<EventsResponse> call = mFacebookApi.getEvents(accessToken);
//
//        Response<EventsResponse> response;
//        try {
//            response = call.execute();
//        } catch (IOException t) {
//            t.printStackTrace();
//            return;
//        }
//
//        if (response.isSuccessful()) {
//            Log.e("FacebookService", "Success. response: " + response.toString());
//        } else {
//            Log.e("FacebookService", "Error. response: " + response.toString());
//
//        }
//    }

    public Completable getEvents(String accessToken) {
        return mFacebookApi.getEvents(accessToken)
                .flatMapObservable(eventsResponse -> {
                    Log.e("FacebooKService", "flatMapObservable, eventSize: " + eventsResponse.getEvents().getEvents().size());
                    return Observable.just(eventsResponse.getEvents().getEvents());
                })
                .flatMapIterable(events -> events)
                .flatMapCompletable(event -> {
                    mDatabaseUtils.convertAndStore(event);
                    return Completable.complete();
                });
    }

}
