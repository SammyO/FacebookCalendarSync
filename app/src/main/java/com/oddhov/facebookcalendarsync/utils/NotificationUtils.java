package com.oddhov.facebookcalendarsync.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.oddhov.facebookcalendarsync.R;
import com.oddhov.facebookcalendarsync.data.Constants;
import com.oddhov.facebookcalendarsync.ui_components.main_activity.MainActivity;

import static android.content.Context.NOTIFICATION_SERVICE;

public class NotificationUtils {
    private static final int REQUEST_CODE = 5;

    private Context mContext;

    public NotificationUtils(Context context) {
        this.mContext = context;
    }

    public void sendNotification(int title, int shortMessage, int longMessage) {

        Intent intent = new Intent(mContext, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, REQUEST_CODE, intent, 0);
        // TODO update notification

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext)
                .setContentTitle(mContext.getString(title))
                .setContentText(mContext.getString(shortMessage))
                .setSmallIcon(R.drawable.ic_sync)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);


        NotificationCompat.BigTextStyle style =
                new NotificationCompat.BigTextStyle();
        style.setBigContentTitle(mContext.getString(title));
        style.bigText(mContext.getString(longMessage));

        mBuilder.setStyle(style);
        Notification notification = mBuilder.build();

        NotificationManager notificationManager =
                (NotificationManager) mContext.getSystemService(NOTIFICATION_SERVICE);

        notificationManager.notify(Constants.NOTIFICATION_ID, notification);
    }

    // TODO this needs to go. For testing only
    public void sendNotification(String title, String shortMessage, String longMessage) {

        Intent intent = new Intent(mContext, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, REQUEST_CODE, intent, 0);
        // TODO update notification

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext)
                .setContentTitle(title)
                .setContentText(shortMessage)
                .setSmallIcon(R.drawable.ic_sync)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);


        NotificationCompat.BigTextStyle style =
                new NotificationCompat.BigTextStyle();
        style.setBigContentTitle(title);
        style.bigText(longMessage);

        mBuilder.setStyle(style);
        Notification notification = mBuilder.build();

        NotificationManager notificationManager =
                (NotificationManager) mContext.getSystemService(NOTIFICATION_SERVICE);

        notificationManager.notify(Constants.NOTIFICATION_ID, notification);
    }
}
