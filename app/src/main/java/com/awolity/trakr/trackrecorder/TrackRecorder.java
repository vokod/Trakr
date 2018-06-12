package com.awolity.trakr.trackrecorder;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;

import com.awolity.trakr.R;
import com.awolity.trakr.data.entity.TrackEntity;
import com.awolity.trakr.data.entity.TrackpointEntity;
import com.awolity.trakr.di.TrakrApplication;
import com.awolity.trakr.location.LocationManager;
import com.awolity.trakr.notification.NotificationUtils;
import com.awolity.trakr.repository.TrackRepository;
import com.awolity.trakr.utils.StringUtils;
import com.google.android.gms.location.LocationRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executor;

import javax.inject.Inject;

public class TrackRecorder implements LocationManager.LocationManagerCallback {

    public static final String TRACKID_BROADCAST_NAME = "com.awolity.trakr.trackrecorder.TrackRecorder.trackIdBroadcast";
    public static final String EXTRA_TRACK_ID = "extra_track_id";
    private static final String TAG = TrackRecorder.class.getSimpleName();
    private TrackEntity track;
    private long trackId;
    private final LocationManager locationManager;
    private Handler handler;
    private final Runnable uiUpdater;
    private final TrackRecorderStatus status;

    @SuppressWarnings("WeakerAccess")
    @Inject
    TrackRepository trackRepository;

    @SuppressWarnings("WeakerAccess")
    @Inject
    Context context;

    @SuppressWarnings("WeakerAccess")
    @Inject
    Executor discIoExecutor;

    TrackRecorder() {
        // MyLog.d(TAG, "TrackRecorder");
        TrakrApplication.getInstance().getAppComponent().inject(this);

        status = new TrackRecorderStatus(context);

        locationManager = new LocationManager(
                status.getTrackingInterval(),
                status.getTrackingInterval() / 2,
                status.getTrackingAccuracy());

        uiUpdater = new Runnable() {
            @Override
            public void run() {
                updateNotification(context, track);
                handler.postDelayed(uiUpdater, 1000);
            }
        };
    }

    void startRecording() {
        // MyLog.d(TAG, "startRecording");

        handler = new Handler();

        discIoExecutor.execute(new Runnable() {
            @Override
            public void run() {
                track = getBaseTrack();
                trackId = trackRepository.saveTrackSync(track);
                track.setTrackId(trackId);

                sendTrackIdBroadcast(context, trackId);

                if (locationManager.isLocationEnabled()) {
                    locationManager.isLocationSettingsGood(new LocationManager.LocationSettingsCallback() {
                        @Override
                        public void onLocationSettingsDetermined(boolean isSettingsGood) {
                            if (isSettingsGood) {
                                // here starts the whole recording
                                locationManager.start(TrackRecorder.this);
                                uiUpdater.run();
                            } else {
                                status.setEverythingGoodForRecording(false);
                            }
                        }
                    });
                } else {
                    status.setEverythingGoodForRecording(false);
                }
                if (!status.isEverythingGoodForRecording()) {
                    // TODO: throw something
                }
            }
        });
    }

    private TrackEntity getBaseTrack() {
        TrackEntity track = new TrackEntity();
        track.setStartTime(System.currentTimeMillis());
        track.setTitle(TrackEntity.getDefaultName(track.getStartTime()));
        track.setMetadata(buildMetadataString());
        return track;
    }

    void stopRecording() {
        locationManager.stop();
        handler.removeCallbacks(uiUpdater);
        checkTrackValidity();
    }

    private void checkTrackValidity() {
        if (status.getNumOfTrackPoints() == 0) {
            trackRepository.deleteTrack(trackId);
        }
        if (status.getNumOfTrackPoints() == 1) {
            // duplicate the only point
            TrackpointEntity trackpointEntity = status.getActualSavedTrackpoint();
            saveTrackAndPointToDb(trackpointEntity);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        // MyLog.d(TAG, "onLocationChanged");

        status.setCandidateTrackpoint(createTrackPoint(location));

        // if accuracy is below the required level, drop the point
        if (!status.isAccurateEnough()) {
            // MyLog.d(TAG, "    - accuracy is below expected - DROPPING");
            return;
        }

        // filtering is only possible if there is a previous data point
        if (status.isThereASavedTrackpoint()) {
            // MyLog.d(TAG, "    - there IS a previous trackpoint ");

            if (status.isDistanceFarEnoughFromLastTrackpoint()) {
                // MyLog.d(TAG, "        - the new location is away from previous, SAVING");

                saveTrackAndPointToDb();
                // updateNotification(context, track);
            } else {
                // MyLog.d(TAG, "        - the new location is exactly the previous, DROPPING");
            }

        } else {
            // MyLog.d(TAG, "    - there is NO previous trackpoint ");
            if (status.getCandidateTrackpoint().getAltitude() != 0) {
                // MyLog.d(TAG, "        - there is no previous trackpoint and this one has valid altitude. SAVING");
                saveTrackAndPointToDb();
                // updateNotification(context, track);
            } else {
                // MyLog.d(TAG, "        - there is no previous trackpoint and this one's altitude is 0. DROPPING");
            }
        }
    }


    private void saveTrackAndPointToDb() {
        saveTrackpointToDb(status.getCandidateTrackpoint());
        status.saveCandidateTrackpoint();
        updateTrackData();
        updateTrackInDb(track);
    }

    private void saveTrackAndPointToDb(TrackpointEntity trackpointEntity) {
        saveTrackpointToDb(trackpointEntity);
        updateTrackData();
        updateTrackInDb(track);
    }

    private void updateTrackData() {
        track.increaseNumOfTrackpoints();
        if (status.isThereASavedTrackpoint()) {
            track.increaseElapsedTime(status.getActualSavedTrackpoint().getTime());
            track.increaseDistance(status.getActualSavedTrackpoint().getDistance());
            track.calculateAscentDescent(status.getActualSavedTrackpoint(), status.getPreviousSavedTrackpoint());
            track.calculateAvgSpeed();
            track.checkSetExtremeValues(status.getActualSavedTrackpoint());
        }
    }

    private TrackpointEntity createTrackPoint(Location location) {
        TrackpointEntity tp = TrackpointEntity.fromLocation(location);
        tp.setTrackId(trackId);
        return tp;
    }

    private void saveTrackpointToDb(TrackpointEntity trackpointEntity) {
        // MyLog.d(TAG, "saveTrackpointToDb");
        trackRepository.saveTrackpoint(trackpointEntity);
    }

    private void updateTrackInDb(TrackEntity track) {
        // MyLog.d(TAG, "updateTrackInDb - trackId:" + track.getTrackId());
        trackRepository.updateTrack(track);
    }

    private String buildMetadataString() {
        String priority;

        switch (locationManager.getLocationRequestPriority()) {
            case LocationRequest.PRIORITY_HIGH_ACCURACY:
                priority = "High accuracy";
                break;
            case LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY:
                priority = "Balanced power accuracy";
                break;
            case LocationRequest.PRIORITY_LOW_POWER:
                priority = "Low power mode";
                break;
            case LocationRequest.PRIORITY_NO_POWER:
                priority = "Passive mode";
                break;
            default:
                priority = "unknown";
        }

        return "Activity type: " +
                status.getActivityType().getKey() +
                ". Tracking interval: "
                + locationManager.getLocationRequestInterval() / 1000
                + "s. "
                + "Minimal distance between two points: "
                + status.getTrackingDistance()
                + "m. "
                + "Geolocation accuracy: "
                + priority
                + ". "
                + "Altitude filter parameter: "
                + status.getAltitudeFilterParameter()
                + ". "
                + "Minimum accuracy to record points: "
                + status.getAccuracyFilterParameter()
                + ". ";
    }

    public long getTrackId() {
        return trackId;
    }

    private static void updateNotification(Context context, TrackEntity track) {
        // MyLog.d(TAG, "updateNotification");
        List<String> lines = new ArrayList<>(6);
        lines.add(context.getString(R.string.record_notification_line_1,
                StringUtils.getElapsedTimeAsString(System.currentTimeMillis() - track.getStartTime())));
        lines.add(context.getString(R.string.record_notification_line_2,
                StringUtils.getDistanceAsThreeCharactersString(track.getDistance())));
        lines.add(context.getString(R.string.record_notification_line_3,
                String.format(Locale.getDefault(), "%.0f", track.getAscent())));
        lines.add(context.getString(R.string.record_notification_line_4,
                String.format(Locale.getDefault(), "%.0f", track.getDescent())));
        lines.add(context.getString(R.string.record_notification_line_5,
                String.format(Locale.getDefault(), "%.1f", track.getAvgSpeed())));

        NotificationUtils.showRecordTrackNotification(context, lines);
    }

    private static void sendTrackIdBroadcast(Context context, long trackId) {
        // MyLog.d(TAG, "sendTrackIdBroadcast - trackId:" + trackId);
        Intent intent = new Intent(TRACKID_BROADCAST_NAME);
        intent.putExtra(EXTRA_TRACK_ID, trackId);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }
}