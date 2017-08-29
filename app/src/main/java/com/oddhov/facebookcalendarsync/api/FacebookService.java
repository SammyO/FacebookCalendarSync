package com.oddhov.facebookcalendarsync.api;


import com.oddhov.facebookcalendarsync.data.models.Event;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class FacebookService {
    private static final int DEFAULT_TIMEOUT = 10;
    private FacebookApi mFacebookApi;

    public FacebookService() {
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

    public Single<List<Event>> getEvents(String accessToken) {
        return mFacebookApi.getEvents(accessToken)
                .flatMap(eventResponse -> Single.just(eventResponse.getEvents()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
