package com.awolity.trakr.trackrecorder;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.res.ResourcesCompat;

import com.awolity.trakr.R;
import com.awolity.trakr.view.main.MainActivity;

import java.util.List;

public class TrakrNotification {

    private static final String CHANNEL_RECORD_TRACK_NOTIFICATION = "record_track_notification";
    static final int ID_NOTIFICATION = 23;

    static void updateNotification(Context context, List<String> lines) {
        createNotificationChannel(context);
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(ID_NOTIFICATION,getNotification(context,lines));
    }

    static Notification getNotification(Context context, List<String> lines) {
        createNotificationChannel(context);
        NotificationCompat.Builder notificationBuilder = getNotificationBuilder(context, lines);
        return notificationBuilder.build();
    }

    private static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            android.app.NotificationManager notificationManager =
                    (android.app.NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            // The user-visible name of the channel.
            CharSequence name = context.getString(R.string.record_track_notification_channel_title);
            // The user-visible description of the channel.
            String description = context.getString(R.string.record_track_notification_channel_description);

            int importance = android.app.NotificationManager.IMPORTANCE_LOW;
            NotificationChannel channel = new NotificationChannel(CHANNEL_RECORD_TRACK_NOTIFICATION,
                    name, importance);
            // Configure the notification channel.
            channel.setDescription(description);
            channel.enableLights(false);
            // Sets the notification light color for notifications posted to this
            // channel, if the device supports this feature.
            channel.enableVibration(false);

            //noinspection ConstantConditions
            notificationManager.createNotificationChannel(channel);
        }
    }

    private static NotificationCompat.Builder getNotificationBuilder(Context context, List<String> lines) {
        BitmapDrawable bitmapDrawable = (BitmapDrawable) context.getResources()
                .getDrawable(R.mipmap.ic_launcher);
        Bitmap largeIcon = bitmapDrawable.getBitmap();

        Intent mainActivityIntent = new Intent(context, MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(mainActivityIntent);
        PendingIntent mainActivityPendingIntent = stackBuilder.getPendingIntent(
                0, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context, CHANNEL_RECORD_TRACK_NOTIFICATION)
                        .setColor(ResourcesCompat.getColor(context.getResources(), R.color.colorPrimaryDark, null))
                        .setSmallIcon(R.drawable.ic_notification)
                        .setLargeIcon(largeIcon)
                        .setContentTitle(context.getString(R.string.record_track_notification_title))
                        .setContentText(context.getString(R.string.record_track_notification_description))
                        .setContentIntent(mainActivityPendingIntent)
                        .setOngoing(true);

        NotificationCompat.InboxStyle inboxStyle =
                new NotificationCompat.InboxStyle();
        inboxStyle.setBigContentTitle(context.getString(R.string.record_track_notification_title));
        inboxStyle.addLine(context.getString(R.string.record_track_notification_description));
        for (String line : lines) {
            inboxStyle.addLine(line);
        }
        notificationBuilder.setStyle(inboxStyle);

        return notificationBuilder;
    }
}
