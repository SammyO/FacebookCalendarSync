package com.oddhov.facebookcalendarsync.utils;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;

import com.oddhov.facebookcalendarsync.data.models.Event;
import com.oddhov.facebookcalendarsync.data.realm_models.RealmCalendarEvent;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

public class DatabaseUtils {
    private Context mContext;
    private Realm mRealm;

    public DatabaseUtils(Context context) {
        this.mContext = context;
        this.mRealm = Realm.getDefaultInstance();
    }

    public void openRealm() {
        if (!mRealm.isClosed()) {
            return;
        }
        mRealm = null;
        mRealm =  Realm.getDefaultInstance();
    }

    public void closeRealm() {
        if (!mRealm.isClosed()) {
            mRealm.close();
        }
    }

    @Nullable
    public List<RealmCalendarEvent> insertAndUpdateCalendarEvents(final List<RealmCalendarEvent> realmCalendarEventsList) {
        if (realmCalendarEventsList.isEmpty()) {
            return null;
        }
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        List<RealmCalendarEvent> updatedEvents = realm.copyToRealmOrUpdate(realmCalendarEventsList);
        realm.commitTransaction();
        realm.close(); //TODO
        if (!updatedEvents.isEmpty()) {
            return updatedEvents;
        }
        return null;
    }

    public List<RealmCalendarEvent> convertToRealmCalendarEvents(List<Event> events) {
        ArrayList<RealmCalendarEvent> realmCalendarEvents = new ArrayList<>();

        if (events != null && events.size() != 0) {
            for (Event event : events) {
                RealmCalendarEvent realmCalendarEvent = new RealmCalendarEvent(
                        event.getId(),
                        event.getName(),
                        event.getDescription(),
                        event.getStartTime(),
                        event.getEndTime(),
                        event.getRsvpStatus()
                );
                realmCalendarEvents.add(realmCalendarEvent);
            }
        }
        return realmCalendarEvents;
    }

    public long getEventsSize() {
        Realm realm = Realm.getDefaultInstance();
        long size = realm.where(RealmCalendarEvent.class).count();
        realm.close();
        return size;
    }

    public List<RealmCalendarEvent> getCalendarEvents() {
        Realm realm = Realm.getDefaultInstance();
        List<RealmCalendarEvent> events = realm.where(RealmCalendarEvent.class).findAll();
        realm.close();
        return events;
    }
}
