package com.awolity.trakr.trackrecorder;

import android.content.Context;
import android.location.Location;

import com.awolity.trakr.activitytype.ActivityType;
import com.awolity.trakr.activitytype.ActivityTypeManager;
import com.awolity.trakr.data.entity.TrackpointEntity;
import com.awolity.trakr.utils.MyLog;
import com.awolity.trakr.utils.PreferenceUtils;

class TrackRecorderStatus {

    private static final String TAG = "TrackRecorderStatus";
    private boolean isEverythingGoodForRecording = true;
    private int trackingDistance;
    private int trackingAccuracy;
    private int trackingInterval;
    private int accuracyFilterParameter;
    private int altitudeFilterParameter;
    private int numOfTrackPoints;
    private TrackpointEntity previousSavedTrackpoint, actualSavedTrackpoint, candidateTrackpoint;
    private AltitudeFilter altitudeFilter;
    private final ActivityType activityType;
    private final AltitudeZeroFilter altitudeZeroFilter;
    private final SpeedZeroFilter speedZeroFilter;

    public TrackRecorderStatus(Context context) {
        MyLog.d(TAG, "TrackRecorderStatus");
        ActivityType activityType1;
        activityType1 = PreferenceUtils.getActivityType(context);
        if (activityType1 == null) {
            activityType1 = ActivityTypeManager.getInstance(context).getActivityTypes().get(0);
        }

        activityType = activityType1;
        setupRecordParameters();

        altitudeFilter = new AltitudeFilter(altitudeFilterParameter);
        altitudeZeroFilter = new AltitudeZeroFilter();
        speedZeroFilter = new SpeedZeroFilter();
    }

    private void setupRecordParameters() {
        MyLog.d(TAG, "setupRecordParameters");
        trackingDistance = activityType.getRecordParameters().getTrackingDistance();
        trackingInterval = activityType.getRecordParameters().getTrackingInterval();
        trackingAccuracy = activityType.getRecordParameters().getTrackingAccuracy();
        accuracyFilterParameter = activityType.getRecordParameters().getAccuracyFilterParameter();
        altitudeFilterParameter = activityType.getRecordParameters().getAltitudeFilterParameter();
    }

    public ActivityType getActivityType() {
        return activityType;
    }

    TrackpointEntity getActualSavedTrackpoint() {
        return actualSavedTrackpoint;
    }

    void setCandidateTrackpoint(TrackpointEntity candidateTrackPoint) {
        MyLog.d(TAG, "setCandidateTrackpoint");
        this.candidateTrackpoint = candidateTrackPoint;

        if (actualSavedTrackpoint != null) {
            candidateTrackPoint.setDistance(getGeologicalDistance(candidateTrackPoint, actualSavedTrackpoint));

            altitudeZeroFilter.filterNext(candidateTrackpoint);
            speedZeroFilter.filterNext(candidateTrackpoint);

            candidateTrackpoint.setAltitude(altitudeFilter.filterNext(
                    candidateTrackpoint.getUnfilteredAltitude()));
        }
    }

    void saveCandidateTrackpoint() {
        MyLog.d(TAG, "saveCandidateTrackpoint");
        if (this.actualSavedTrackpoint != null) {
            previousSavedTrackpoint = this.actualSavedTrackpoint;
        }
        actualSavedTrackpoint = candidateTrackpoint;
        candidateTrackpoint = null;
        numOfTrackPoints++;
    }

    int getNumOfTrackPoints() {
        return numOfTrackPoints;
    }

    TrackpointEntity getCandidateTrackpoint() {
        return candidateTrackpoint;
    }

    boolean isDistanceFarEnoughFromLastTrackpoint() {
        return candidateTrackpoint.getDistance() > getTrackingDistance();
    }

    boolean isAccurateEnough() {
        return candidateTrackpoint.getAccuracy() < getAccuracyFilterParameter();
    }

    boolean isThereASavedTrackpoint() {
        return actualSavedTrackpoint != null;
    }

    boolean isEverythingGoodForRecording() {
        return isEverythingGoodForRecording;
    }

    void setEverythingGoodForRecording(boolean everythingGoodForRecording) {
        isEverythingGoodForRecording = everythingGoodForRecording;
    }

    int getTrackingDistance() {
        return trackingDistance;
    }

    int getTrackingAccuracy() {
        return trackingAccuracy;
    }

    int getTrackingInterval() {
        return trackingInterval;
    }

    int getAccuracyFilterParameter() {
        return accuracyFilterParameter;
    }

    int getAltitudeFilterParameter() {
        return altitudeFilterParameter;
    }

    TrackpointEntity getPreviousSavedTrackpoint() {
        return previousSavedTrackpoint;
    }

    private static double getGeologicalDistance(TrackpointEntity actualTrackpoint,
                                                TrackpointEntity previousTrackpoint) {
        MyLog.d(TAG, "getGeologicalDistance");
        Location previousLocation = new Location("previousTrackpointLocation");
        previousLocation.setLatitude(previousTrackpoint.getLatitude());
        previousLocation.setLongitude(previousTrackpoint.getLongitude());

        Location actualLocation = new Location("actualTrackpointLocation");
        actualLocation.setLatitude(actualTrackpoint.getLatitude());
        actualLocation.setLongitude(actualTrackpoint.getLongitude());
        return actualLocation.distanceTo(previousLocation);
    }
}