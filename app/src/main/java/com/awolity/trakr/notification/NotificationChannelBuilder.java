package com.awolity.trakr.notification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;

class NotificationChannelBuilder {

    private final NotificationChannel channel;

    @RequiresApi(Build.VERSION_CODES.O)
    public NotificationChannelBuilder(String id, String name) {
        channel = new NotificationChannel(id, name, NotificationManager.IMPORTANCE_DEFAULT);
    }

    @RequiresApi(Build.VERSION_CODES.O)
    public NotificationChannelBuilder setDescription(String description) {
        channel.setDescription(description);
        return this;
    }

    @RequiresApi(Build.VERSION_CODES.O)
    public NotificationChannelBuilder setImportance(int importance) {
        channel.setImportance(importance);
        return this;
    }

    @RequiresApi(Build.VERSION_CODES.O)
    public NotificationChannelBuilder enableLights(boolean enableLights) {
        channel.enableLights(enableLights);
        return this;
    }

    @RequiresApi(Build.VERSION_CODES.O)
    public NotificationChannelBuilder enableVibration(boolean enableVibration) {
        channel.enableVibration(enableVibration);
        return this;
    }

    @RequiresApi(Build.VERSION_CODES.O)
    public NotificationChannelBuilder setLightColor(int color) {
        channel.setLightColor(color);
        return this;
    }

    @RequiresApi(Build.VERSION_CODES.O)
    public NotificationChannelBuilder setVibrationPattern(long[] vibrationPattern) {
        channel.setVibrationPattern(vibrationPattern);
        return this;
    }

    @RequiresApi(Build.VERSION_CODES.O)
    public void buildAndSet(Context context) {
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(channel);
    }
}
