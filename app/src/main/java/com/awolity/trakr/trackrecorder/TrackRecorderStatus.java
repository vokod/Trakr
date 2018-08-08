package com.awolity.trakr.trackrecorder;

import android.content.Context;
import android.location.Location;

import com.awolity.trakr.data.entity.TrackpointEntity;
import com.awolity.trakr.utils.MyLog;
import com.awolity.trakr.utils.PreferenceUtils;
import com.google.android.gms.location.LocationRequest;

class TrackRecorderStatus {

    private static final String TAG = "TrackRecorderStatus";
    private boolean isEverythingGoodForRecording = true;
    private int numOfTrackPoints;
    private TrackpointEntity previousSavedTrackpoint, actualSavedTrackpoint, candidateTrackpoint;
    private LowPassFilter altitudeFilter, speedFilter;
    private final AltitudeZeroFilter altitudeZeroFilter;

    public TrackRecorderStatus(Context context) {
        // MyLog.d(TAG, "TrackRecorderStatus");
        altitudeFilter = new LowPassFilter(10);
        speedFilter = new LowPassFilter(2);
        altitudeZeroFilter = new AltitudeZeroFilter();
    }

    TrackpointEntity getActualSavedTrackpoint() {
        return actualSavedTrackpoint;
    }

    void setCandidateTrackpoint(TrackpointEntity candidateTrackpoint) {
        // MyLog.d(TAG, "setCandidateTrackpoint");
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
        // MyLog.d(TAG, "saveCandidateTrackpoint");
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
        return 3;
    }

    int getTrackingAccuracy() {
        return LocationRequest.PRIORITY_HIGH_ACCURACY;
    }

    int getTrackingInterval() {
        return 3;
    }

    int getMinimalRecordAccuracy() {
        return 20;
    }

    int getAltitudeFilterParameter() {
        return 10;
    }

    int getSpeedFilterParameter() {
        return 2;
    }

    TrackpointEntity getPreviousSavedTrackpoint() {
        return previousSavedTrackpoint;
    }

    private static double getGeologicalDistance(TrackpointEntity actualTrackpoint,
                                                TrackpointEntity previousTrackpoint) {
        // MyLog.d(TAG, "getGeologicalDistance");
        Location previousLocation = new Location("previousTrackpointLocation");
        previousLocation.setLatitude(previousTrackpoint.getLatitude());
        previousLocation.setLongitude(previousTrackpoint.getLongitude());

        Location actualLocation = new Location("actualTrackpointLocation");
        actualLocation.setLatitude(actualTrackpoint.getLatitude());
        actualLocation.setLongitude(actualTrackpoint.getLongitude());
        return actualLocation.distanceTo(previousLocation);
    }
}