package com.oddhov.facebookcalendarsync.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.oddhov.facebookcalendarsync.data.Constants;

import static android.content.Context.NOTIFICATION_SERVICE;

public class NotificationUtils {
    private static final int REQUEST_CODE = 5;

    private Context mContext;

    public NotificationUtils(Context context) {
        this.mContext = context;
    }

    public void sendNotification(int title, int shortMessage, int longMessage, int icon, Class targetClass) {
        Intent intent = new Intent(mContext, targetClass);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, REQUEST_CODE, intent, 0);
        // TODO update notification

        Notification notification = new Notification.Builder(mContext)
                .setContentTitle(mContext.getString(title))
                .setContentText(mContext.getString(shortMessage))
                .setSmallIcon(icon)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build();

        NotificationManager notificationManager =
                (NotificationManager) mContext.getSystemService(NOTIFICATION_SERVICE);

        notificationManager.notify(Constants.NOTIFICATION_ID, notification);
    }
}
