package com.awolity.trakr.trackrecorder;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Binder;

import com.awolity.trakr.R;
import com.awolity.trakr.notification.NotificationUtils;

import java.util.ArrayList;
import java.util.List;

public class TrackRecorderService extends Service {

    public static final String TAG = TrackRecorderService.class.getSimpleName();
    private TrackRecorder recorder;
    private final TrackRecorderServiceBinder binder = new TrackRecorderServiceBinder();

    @Override
    public void onDestroy() {
        super.onDestroy();
         // MyLog.d(TAG, "onDestroy");
        stopRecordTrack();
    }

    @Override
    public TrackRecorderServiceBinder onBind(Intent intent) {
         // MyLog.d(TAG, "onBind");
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
         // MyLog.d(TAG, "onUnbind");
        stopRecordTrack();
        return false;
    }

    @Override
    public void onCreate() {
        super.onCreate();
         // MyLog.d(TAG, "onCreate");
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
         // MyLog.d(TAG, "onConfigurationChanged");
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
         // MyLog.d(TAG, "onRebind");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForeground(NotificationUtils.NOTIFICATION_ID_TRACK_RECORD,
                NotificationUtils.getRecordTrackNotification(this, getNotificationDetails(this)));
        startRecordTrack();
        return START_STICKY;
    }

    public long getTrackID() {
         // MyLog.d(TAG, "getTrackID - hash:" + this.hashCode());
        return recorder.getTrackId();
    }

    private void startRecordTrack() {
         // MyLog.d(TAG, "startRecordTrack");
        recorder = new TrackRecorder();
        recorder.startRecording();
    }

    private void stopRecordTrack() {
         // MyLog.d(TAG, "stopRecordTrack");
        if (recorder != null) {
            recorder.stopRecording();
            recorder = null;
        }
        stopForeground(true);
    }

    class TrackRecorderServiceBinder extends Binder {
        TrackRecorderService getService() {
            return TrackRecorderService.this;
        }
    }

    private static List<String> getNotificationDetails(Context context) {
        List<String> lines = new ArrayList<>(6);
        lines.add(context.getString(R.string.record_notification_line_1,"0"));
        lines.add(context.getString(R.string.record_notification_line_2,"0"));
        lines.add(context.getString(R.string.record_notification_line_3,"0"));
        lines.add(context.getString(R.string.record_notification_line_4,"0"));
        lines.add(context.getString(R.string.record_notification_line_5,"-"));
        return lines;
    }
}
