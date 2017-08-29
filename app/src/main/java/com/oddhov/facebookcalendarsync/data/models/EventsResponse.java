
package com.oddhov.facebookcalendarsync.data.models;

import com.google.gson.annotations.SerializedName;

public class EventsResponse {

    @SerializedName("events")
    private Events events;
    @SerializedName("id")
    private String id;

    public Events getEvents() {
        return events;
    }

    public void setEvents(Events events) {
        this.events = events;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
