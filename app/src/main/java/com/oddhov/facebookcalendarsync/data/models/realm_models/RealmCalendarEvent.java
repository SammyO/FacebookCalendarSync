package com.oddhov.facebookcalendarsync.data.models.realm_models;

import com.oddhov.facebookcalendarsync.data.models.RsvpStatus;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class RealmCalendarEvent extends RealmObject {
    @PrimaryKey
    private String id;
    private String name;
    private String description;
//    private Place place; // TODO
    private String startTime;
    private String endTime;
    private String rsvpStatus;

    public RealmCalendarEvent() {
    }

    public RealmCalendarEvent(String id, String name, String description, String startTime, String endTime,
                              RsvpStatus rsvpStatus) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.startTime = startTime;
        this.endTime = endTime;
        this.rsvpStatus = rsvpStatus.getFacebookParameter();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public RsvpStatus getRsvpStatus() {
        return RsvpStatus.getEnumFromFacebookParameter(rsvpStatus);
    }
}
