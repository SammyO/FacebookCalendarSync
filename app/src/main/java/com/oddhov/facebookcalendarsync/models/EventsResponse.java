
package com.oddhov.facebookcalendarsync.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventsResponse {

    @SerializedName("data")
    private List<Event> events = new ArrayList<>();
    private Paging paging;
    private Map<String, Object> additionalProperties = new HashMap<>();

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

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
