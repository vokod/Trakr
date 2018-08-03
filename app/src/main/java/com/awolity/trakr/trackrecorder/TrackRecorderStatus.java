package com.awolity.trakr.trackrecorder;

import android.content.Context;
import android.location.Location;

import com.awolity.trakr.activitytype.ActivityType;
import com.awolity.trakr.activitytype.ActivityTypeManager;
import com.awolity.trakr.data.entity.TrackpointEntity;
import com.awolity.trakr.utils.PreferenceUtils;

class TrackRecorderStatus {

    private boolean isAltitudeFiltered, speedZeroFilterAlreadyapplied;
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

    public TrackRecorderStatus(Context context) {
        ActivityType activityType1;
        activityType1 = PreferenceUtils.getActivityType(context);
        if (activityType1 == null) {
            activityType1 = ActivityTypeManager.getInstance(context).getActivityTypes().get(0);
        }

        activityType = activityType1;
        setupRecordParameters();

        if (isAltitudeFiltered) {
            altitudeFilter = new AltitudeFilter(altitudeFilterParameter);
        }
    }

    private void setupRecordParameters() {
        trackingDistance = activityType.getRecordParameters().getTrackingDistance();
        trackingInterval = activityType.getRecordParameters().getTrackingInterval();
        trackingAccuracy = activityType.getRecordParameters().getTrackingAccuracy();
        isAltitudeFiltered = true;
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
        this.candidateTrackpoint = candidateTrackPoint;

        if (actualSavedTrackpoint != null) {
            candidateTrackPoint.setDistance(getGeologicalDistance(candidateTrackPoint, actualSavedTrackpoint));

            applyAltitudeZeroFilter();
            applyAltitudeFilter();
            applySpeedZeroFilter();
        }
    }

    void saveCandidateTrackpoint() {
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

    private static double getGeologicalDistance(TrackpointEntity actualTrackpoint, TrackpointEntity previousTrackpoint) {
        Location previousLocation = new Location("previousTrackpointLocation");
        previousLocation.setLatitude(previousTrackpoint.getLatitude());
        previousLocation.setLongitude(previousTrackpoint.getLongitude());

        Location actualLocation = new Location("actualTrackpointLocation");
        actualLocation.setLatitude(actualTrackpoint.getLatitude());
        actualLocation.setLongitude(actualTrackpoint.getLongitude());
        return actualLocation.distanceTo(previousLocation);
    }

    private void applyAltitudeZeroFilter() {
        if (candidateTrackpoint.getUnfilteredAltitude() == 0) {
            candidateTrackpoint.setUnfilteredAltitude(actualSavedTrackpoint.getAltitude());
        }
    }

    private void applyAltitudeFilter(){
        if (isAltitudeFiltered) {
            candidateTrackpoint.setAltitude(altitudeFilter.filterNext(
                    candidateTrackpoint.getUnfilteredAltitude()));
        }
    }

    private void applySpeedZeroFilter(){
        // apply speed zero filter: if candidate trackpoint speed is 0,
        // then set it to previous point's speed.
        // but only do it once.
        if (candidateTrackpoint.getSpeed() == 0 && !speedZeroFilterAlreadyapplied) {
            speedZeroFilterAlreadyapplied = true;
            candidateTrackpoint.setSpeed(actualSavedTrackpoint.getSpeed());
        } else {
            speedZeroFilterAlreadyapplied = false;
        }
    }
}