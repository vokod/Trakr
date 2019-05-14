package com.awolity.trakr.trackrecorder;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Binder;
import android.os.Build;
import android.os.PowerManager;

import com.awolity.trakr.R;
import com.awolity.trakr.notification.NotificationUtils;
import com.awolity.trakr.utils.MyLog;

import java.util.ArrayList;
import java.util.List;

public class TrackRecorderService extends Service {

    public static final String TAG = TrackRecorderService.class.getSimpleName();
    public static final String WAKELOCK_TAG = "Trakr::WakelockTag";
    private TrackRecorder recorder;
    private final TrackRecorderServiceBinder binder = new TrackRecorderServiceBinder();
    private PowerManager pwrMngr;
    private PowerManager.WakeLock wakeLock;

    @Override
    public void onDestroy() {
        super.onDestroy();
        MyLog.d(TAG, "onDestroy");
        stopRecordTrack();
    }

    @Override
    public TrackRecorderServiceBinder onBind(Intent intent) {
        MyLog.d(TAG, "onBind");
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        MyLog.d(TAG, "onUnbind");
        stopRecordTrack();
        return false;
    }

    @SuppressWarnings("EmptyMethod")
    @Override
    public void onCreate() {
        super.onCreate();
        MyLog.d(TAG, "onCreate");
    }

    @SuppressWarnings("EmptyMethod")
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        MyLog.d(TAG, "onConfigurationChanged");
    }

    @SuppressWarnings("EmptyMethod")
    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
        MyLog.d(TAG, "onRebind");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForeground(NotificationUtils.NOTIFICATION_ID_TRACK_RECORD,
                NotificationUtils.getRecordTrackNotification(this, getNotificationDetails(this)));
        acquireWakeLock();
        startRecordTrack();
        return START_STICKY;
    }

    @SuppressLint("WakelockTimeout")
    private void acquireWakeLock() {
        MyLog.d(TAG, "acquireWakeLock");
        pwrMngr = (PowerManager) getSystemService(POWER_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            wakeLock = pwrMngr.newWakeLock(PowerManager.LOCATION_MODE_NO_CHANGE
                            | PowerManager.PARTIAL_WAKE_LOCK,
                    WAKELOCK_TAG);
        } else {
            wakeLock = pwrMngr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    WAKELOCK_TAG);
        }
        wakeLock.acquire();
    }

    private void releaseWakeLock() {
        MyLog.d(TAG, "releaseWakeLock");
        wakeLock.release();
    }

    private void startRecordTrack() {
        MyLog.d(TAG, "startRecordTrack");
        recorder = new TrackRecorder();
        recorder.startRecording();
    }

    private void stopRecordTrack() {
        MyLog.d(TAG, "stopRecordTrack");
        if (recorder != null) {
            recorder.stopRecording();
            recorder = null;
        }
        releaseWakeLock();
        stopForeground(true);
    }

    class TrackRecorderServiceBinder extends Binder {
        TrackRecorderService getService() {
            return TrackRecorderService.this;
        }
    }

    private static List<String> getNotificationDetails(Context context) {
        List<String> lines = new ArrayList<>(6);
        lines.add(context.getString(R.string.record_notification_line_1, "0"));
        lines.add(context.getString(R.string.record_notification_line_2, "0"));
        lines.add(context.getString(R.string.record_notification_line_3, "0"));
        lines.add(context.getString(R.string.record_notification_line_4, "0"));
        lines.add(context.getString(R.string.record_notification_line_5, "-"));
        return lines;
    }
}
