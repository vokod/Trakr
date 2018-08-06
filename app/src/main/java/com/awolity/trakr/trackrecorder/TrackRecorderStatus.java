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
    private int numOfTrackPoints;
    private TrackpointEntity previousSavedTrackpoint, actualSavedTrackpoint, candidateTrackpoint;
    private LowPassFilter altitudeFilter, speedFilter;
    private final ActivityType activityType;
    private final AltitudeZeroFilter altitudeZeroFilter;

    public TrackRecorderStatus(Context context) {
        // MyLog.d(TAG, "TrackRecorderStatus");
        ActivityType activityType1;
        activityType1 = PreferenceUtils.getActivityType(context);
        if (activityType1 == null) {
            activityType1 = ActivityTypeManager.getInstance(context).getActivityTypes().get(0);
        }

        activityType = activityType1;

        altitudeFilter = new LowPassFilter(
                activityType.getRecordParameters().getAltitudeFilterParameter());
        speedFilter = new LowPassFilter(
                activityType.getRecordParameters().getSpeedFilterParameter());
        altitudeZeroFilter = new AltitudeZeroFilter();
    }

    public ActivityType getActivityType() {
        return activityType;
    }

    TrackpointEntity getActualSavedTrackpoint() {
        return actualSavedTrackpoint;
    }

    void setCandidateTrackpoint(TrackpointEntity candidateTrackpoint) {
        MyLog.d(TAG, "setCandidateTrackpoint");
        this.candidateTrackpoint = candidateTrackpoint;

        if (actualSavedTrackpoint != null) {
            this.candidateTrackpoint.setDistance(
                    getGeologicalDistance(candidateTrackpoint, actualSavedTrackpoint));

            altitudeZeroFilter.filterNext(this.candidateTrackpoint);

            this.candidateTrackpoint.setAltitude(altitudeFilter.filterNext(
                    this.candidateTrackpoint.getUnfilteredAltitude()));

            this.candidateTrackpoint.setSpeed(speedFilter.filterNext(
                    this.candidateTrackpoint.getUnfilteredSpeed()));
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
        return candidateTrackpoint.getAccuracy() < getMinimalRecordAccuracy();
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
        return activityType.getRecordParameters().getTrackingDistance();
    }

    int getTrackingAccuracy() {
        return activityType.getRecordParameters().getTrackingAccuracy();
    }

    int getTrackingInterval() {
        return activityType.getRecordParameters().getTrackingInterval();
    }

    int getMinimalRecordAccuracy() {
        return activityType.getRecordParameters().getMinimalRecordAccuracy();
    }

    int getAltitudeFilterParameter() {
        return activityType.getRecordParameters().getAltitudeFilterParameter();
    }

    int getSpeedFilterParameter() {
        return activityType.getRecordParameters().getSpeedFilterParameter();
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