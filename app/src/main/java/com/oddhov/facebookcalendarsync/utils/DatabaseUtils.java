package com.oddhov.facebookcalendarsync.utils;

import android.content.Context;

import com.oddhov.facebookcalendarsync.data.models.Event;
import com.oddhov.facebookcalendarsync.data.realm_models.CalendarEvent;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class DatabaseUtils {
    private Context mContext;
    private Realm mRealm;
    private RealmConfiguration mRealmConfiguration;

    public DatabaseUtils(Context context) {
        this.mContext = context;
    }

    public void initializeRealmConfig(Context appContext) {
        if(mRealmConfiguration == null) {
            Realm.init(mContext);
            mRealmConfiguration = new RealmConfiguration.Builder()
                    .deleteRealmIfMigrationNeeded() // TODO
                    .build();
            Realm.setDefaultConfiguration(mRealmConfiguration);
        }
    }

    public void insertAndUpdateCalendarEvents(final List<CalendarEvent> calendarEventsList) {
        if (calendarEventsList.isEmpty()) {
            return;
        }

        mRealm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.delete(CalendarEvent.class);
                realm.insertOrUpdate(calendarEventsList);
                // TODO optimise this
            }
        });
        mRealm.close();
    }

    public List<CalendarEvent> convertCalendarEvents(List<Event> events) {
        ArrayList<CalendarEvent> calendarEvents = new ArrayList<>();

        if (events != null && events.size() != 0) {
            for (Event event : events) {
                CalendarEvent calendarEvent = new CalendarEvent(
                        event.getId(),
                        event.getName(),
                        event.getDescription(),
                        event.getStartTime(),
                        event.getEndTime(),
                        event.getRsvpStatus()
                );
                calendarEvents.add(calendarEvent);
            }
        }
        return calendarEvents;
    }

    public long getEventsSize() {
        return mRealm.where(CalendarEvent.class).count();
    }
}
