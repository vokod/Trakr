package com.awolity.trakr.trackrecorder;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.awolity.trakr.di.TrakrApplication;
import com.awolity.trakr.utils.MyLog;
import com.awolity.trakr.utils.PreferenceUtils;

import javax.inject.Inject;

public class TrackRecorderServiceManager {

    private static final String TAG = TrackRecorderServiceManager.class.getSimpleName();
    private TrackRecorderServiceManagerListener listener;
    private BroadcastReceiver trackIdBroadcastReceiver;
    private boolean isStarted;

    @SuppressWarnings("WeakerAccess")
    @Inject
    Context context;

    public TrackRecorderServiceManager(final TrackRecorderServiceManagerListener listener) {
        // MyLog.d(TAG, "TrackRecorderServiceManager");
        TrakrApplication.getInstance().getAppComponent().inject(this);
        this.listener = listener;

        isStarted = isServiceRunning(context);

        trackIdBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                long id = intent.getLongExtra(TrackRecorder.EXTRA_TRACK_ID, -1);
                LocalBroadcastManager.getInstance(context).unregisterReceiver(trackIdBroadcastReceiver);
                Log.d(TAG, "onReceive - trackId:" + id);
                PreferenceUtils.setLastRecordedTrackId(context, id);
                listener.onServiceStarted(id);
                isStarted = true;
            }
        };
    }

    public void startStopFabClicked() {
        MyLog.d(TAG, "startStopFabClicked");
        if (isStarted) {
            MyLog.d(TAG, "startStopFabClicked - service IS running");
            stopService();
        } else {
            MyLog.d(TAG, "startStopFabClicked - service is NOT running");
            LocalBroadcastManager.getInstance(context).registerReceiver(trackIdBroadcastReceiver,
                    new IntentFilter(TrackRecorder.TRACKID_BROADCAST_NAME));
            startService();

        }
    }

    private void startService() {
        MyLog.d(TAG, "startService - enter");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(getServiceIntent(context));
        } else {
            context.startService(getServiceIntent(context));
        }
    }

    private void stopService() {
        MyLog.d(TAG, "stopService");
        context.stopService(getServiceIntent(context));
        isStarted = false;
        listener.onServiceStopped();
    }

    private static Intent getServiceIntent(Context context) {
        return new Intent(context, TrackRecorderService.class);
    }

    public static boolean isServiceRunning(Context context) {
        // MyLog.d(TAG, "isServiceRunning");
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (TrackRecorderService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public interface TrackRecorderServiceManagerListener {
        void onServiceStarted(long trackId);

        void onServiceStopped();
    }
}
