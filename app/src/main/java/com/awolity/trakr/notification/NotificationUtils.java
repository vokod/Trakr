package com.awolity.trakr.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.content.res.ResourcesCompat;

import com.awolity.trakr.R;
import com.awolity.trakr.view.detail.TrackDetailActivity;
import com.awolity.trakr.view.main.MainActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static android.support.v4.app.NotificationManagerCompat.IMPORTANCE_LOW;

public class NotificationUtils {

    private static final String NOTIFICATION_CHANNEL_ID_TRACK_RECORD = "com.awolity.trakr.notification.track_recording";
    private static final String NOTIFICATION_CHANNEL_ID_TRACK_EXPORT = "com.awolity.trakr.notification.track_exporting";
    public static final int NOTIFICATION_ID_TRACK_RECORD = 20;
    private static final int NOTIFICATION_ID_TRACK_EXPORT = 21;
    private static final String LOG_TAG = NotificationUtils.class.getSimpleName();

    public static void showRecordTrackNotification(Context context, List<String> lines) {
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID_TRACK_RECORD, getRecordTrackNotification(context, lines));
    }

    public static Notification getRecordTrackNotification(Context context,List<String> lines ){
        Intent intent = new Intent(context, MainActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntentWithParentStack(intent);
        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID_TRACK_RECORD)
                        .setColor(ResourcesCompat.getColor(context.getResources(), R.color.colorPrimaryDark, null))
                        .setSmallIcon(R.drawable.ic_notification)
                        .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),
                                R.mipmap.ic_launcher))
                        .setContentTitle(context.getString(R.string.record_track_notification_title))
                        .setContentText(lines.get(0))
                        .setContentIntent(pendingIntent)
                        .setOngoing(true);

        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        inboxStyle.setBigContentTitle(context.getString(R.string.record_track_notification_title));
        //inboxStyle.addLine(context.getString(R.string.record_track_notification_description));
        for (String line : lines) {
            inboxStyle.addLine(line);
        }
        notificationBuilder.setStyle(inboxStyle);

        return notificationBuilder.build();
    }

    public static void showExportTrackNotification(Context context, long trackId, String fileName, String path) {
        Intent intent = TrackDetailActivity.getStarterIntent(context, trackId);

        // Create the TaskStackBuilder and add the intent, which inflates the back stack
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntentWithParentStack(intent);
        // Get the PendingIntent containing the entire back stack
        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID_TRACK_EXPORT)
                        .setColor(ResourcesCompat.getColor(context.getResources(), R.color.colorPrimaryDark, null))
                        .setSmallIcon(R.drawable.ic_notification)
                        .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),
                                R.mipmap.ic_launcher))
                        .setContentTitle(context.getString(R.string.export_track_notification_title))
                        .setContentText(context.getString(R.string.export_track_notification_description))
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent)
                        .setOngoing(true);

        List<String> notificationLines = new ArrayList<>(2);
        notificationLines.add(context.getResources().getString(
                R.string.export_track_notification_expanded_line_1,
                fileName));
        notificationLines.add(context.getResources().getString(
                R.string.export_track_notification_expanded_line_2,
                path));

        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        // Sets a title for the Inbox in expanded layout
        inboxStyle.setBigContentTitle(context.getString(R.string.export_track_notification_title));
        for (String line : notificationLines) {
            inboxStyle.addLine(line);
        }
        notificationBuilder.setStyle(inboxStyle);

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID_TRACK_EXPORT, notificationBuilder.build());
    }

    public static void showExportTrackErrorNotification(Context context, long trackId, String fileName, String path) {
        Intent intent = TrackDetailActivity.getStarterIntent(context, trackId);

        // Create the TaskStackBuilder and add the intent, which inflates the back stack
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntentWithParentStack(intent);
        // Get the PendingIntent containing the entire back stack
        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID_TRACK_EXPORT)
                        .setColor(ResourcesCompat.getColor(context.getResources(), R.color.colorPrimaryDark, null))
                        .setSmallIcon(R.drawable.ic_notification)
                        .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),
                                R.mipmap.ic_launcher))
                        .setContentTitle(context.getString(R.string.export_track_error_notification_title))
                        .setContentText(context.getString(R.string.export_track_error_notification_description))
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent);


        List<String> notificationLines = new ArrayList<>(2);
        notificationLines.add(context.getResources().getString(
                R.string.export_track_error_notification_expanded_line_1,
                fileName));
        notificationLines.add(context.getResources().getString(
                R.string.export_track_error_notification_expanded_line_2,
                path));

        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        // Sets a title for the Inbox in expanded layout
        inboxStyle.setBigContentTitle(context.getString(R.string.export_track_error_notification_title));
        for (String line : notificationLines) {
            inboxStyle.addLine(line);
        }
        notificationBuilder.setStyle(inboxStyle);

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID_TRACK_EXPORT, notificationBuilder.build());
    }

    public static void showExportTrackDoneNotification(Context context, String fileName, String path) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        Uri fileUri = FileProvider.getUriForFile(context,
                context.getApplicationContext().getPackageName() + ".gpx.GpxFileProvider",
                new File(path + fileName));
        intent.setDataAndType(fileUri, "*/*");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID_TRACK_EXPORT)
                        .setColor(ResourcesCompat.getColor(context.getResources(), R.color.colorPrimaryDark, null))
                        .setSmallIcon(R.drawable.ic_notification)
                        .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),
                                R.mipmap.ic_launcher))
                        .setContentTitle(context.getString(R.string.export_track_done_notification_title))
                        .setContentText(context.getString(R.string.export_track_done_notification_description))
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent);


        List<String> notificationLines = new ArrayList<>(2);
        notificationLines.add(context.getResources().getString(
                R.string.export_track_done_notification_expanded_line_1, fileName));
        notificationLines.add(context.getResources().getString(
                R.string.export_track_done_notification_expanded_line_2,
                path));

        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        // Sets a title for the Inbox in expanded layout
        inboxStyle.setBigContentTitle(context.getString(R.string.export_track_done_notification_title));
        for (String line : notificationLines) {
            inboxStyle.addLine(line);
        }
        notificationBuilder.setStyle(inboxStyle);

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID_TRACK_EXPORT, notificationBuilder.build());
    }

    public static void setupNotificationChannels(Context context) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            new NotificationChannelBuilder(
                    NOTIFICATION_CHANNEL_ID_TRACK_RECORD,
                    context.getString(R.string.record_track_notification_channel_title))
                    .enableLights(false)
                    .enableVibration(false)
                    .setImportance(IMPORTANCE_LOW)
                    .setDescription(context.getString(R.string.record_track_notification_channel_description))
                    .buildAndSet(context);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            new NotificationChannelBuilder(
                    NOTIFICATION_CHANNEL_ID_TRACK_EXPORT,
                    context.getString(R.string.export_track_notification_channel_title))
                    .enableLights(false)
                    .enableVibration(false)
                    .setImportance(IMPORTANCE_LOW)
                    .setDescription(context.getString(R.string.export_track_notification_channel_description))
                    .buildAndSet(context);
        }
    }


}
