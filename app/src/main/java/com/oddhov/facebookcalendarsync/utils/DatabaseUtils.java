package com.oddhov.facebookcalendarsync.utils;

import android.content.Context;

import com.oddhov.facebookcalendarsync.data.models.Event;
import com.oddhov.facebookcalendarsync.data.realm_models.RealmCalendarEvent;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.Sort;

public class DatabaseUtils {
    private Context mContext;
    private RealmConfiguration mRealmConfiguration;

    public DatabaseUtils(Context context) {
        this.mContext = context;
    }

    public void initializeRealmConfig(Context appContext) {
        if (mRealmConfiguration == null) {
            Realm.init(mContext);
            mRealmConfiguration = new RealmConfiguration.Builder()
                    .deleteRealmIfMigrationNeeded() // TODO
                    .build();
            Realm.setDefaultConfiguration(mRealmConfiguration);
        }
    }

    public void insertAndUpdateCalendarEvents(final List<RealmCalendarEvent> realmCalendarEventsList) {
        if (realmCalendarEventsList.isEmpty()) {
            return;
        }
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.insertOrUpdate(realmCalendarEventsList);
                // TODO no longer existing events should be removed
            }
        });
        realm.close();
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
        return realm.where(RealmCalendarEvent.class).findAll();
    }
}
