package com.oddhov.facebookcalendarsync.utils;

import android.content.Context;
import android.support.annotation.Nullable;

import com.oddhov.facebookcalendarsync.data.exceptions.RealmException;
import com.oddhov.facebookcalendarsync.data.models.Event;
import com.oddhov.facebookcalendarsync.data.models.realm_models.RealmCalendarEvent;
import com.oddhov.facebookcalendarsync.data.models.realm_models.UserData;

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
        mRealm = Realm.getDefaultInstance();
    }

    public void closeRealm() {
        if (!mRealm.isClosed()) {
            mRealm.close();
        }
    }

    public void setupUserData() throws RealmException {
        openRealm();
        if (getUserData() == null) {
            mRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    mRealm.createObject(UserData.class);
                }
            });
        }
        closeRealm();
    }

    public void setLastSynced(final long timeStamp) throws RealmException {
        mRealm = Realm.getDefaultInstance();
        final UserData userData = getUserData();
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                userData.setLastSyncedTimeStamp(timeStamp);
            }
        });
        closeRealm();
    }

    public Long getLastSynced() throws RealmException {
        mRealm = Realm.getDefaultInstance();
        Long lastSyncedTimeStamp = getUserData().getLastSyncedTimeStamp();
        closeRealm();
        return lastSyncedTimeStamp;
    }

    @Nullable
    public List<RealmCalendarEvent> updateCalendarEvents(List<RealmCalendarEvent> realmCalendarEventsList) {
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

    public void setEventEndTime(final RealmCalendarEvent event, final String endTime) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                event.setEndTime(endTime);
            }
        });
        realm.close();
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

    private UserData getUserData() throws RealmException {
        long instances = mRealm.where(UserData.class).count();
        if (instances <= 1) {
            return mRealm.where(UserData.class).findFirst();
        } else {
            throw new RealmException("More than one UserData instance");
        }
    }
}
