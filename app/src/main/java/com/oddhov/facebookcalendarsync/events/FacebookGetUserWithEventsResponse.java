package com.oddhov.facebookcalendarsync.events;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.oddhov.facebookcalendarsync.models.EventsResponse;

public class FacebookGetUserWithEventsResponse {

    public static EventsResponse parseJSON(String response) {
        String ISO_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ";

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setDateFormat(ISO_FORMAT);
        Gson gson = gsonBuilder.create();

        return gson.fromJson(response, EventsResponse.class);
    }
}
