package com.oddhov.facebookcalendarsync.data.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Events {
    @SerializedName("data")
    private List<Event> events = new ArrayList<>();
    private Paging paging;

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> data) {
        this.events = data;
    }

    public Paging getPaging() {
        return paging;
    }

    public void setPaging(Paging paging) {
        this.paging = paging;
    }

}
