package com.awolity.trakr.trackrecorder;

import android.location.Location;

import com.awolity.trakr.TrakrApplication;
import com.awolity.trakr.data.entity.TrackpointEntity;
import com.awolity.trakr.repository.SettingsRepository;
import com.awolity.trakr.utils.RecordParameters;

import javax.inject.Inject;

public class TrackRecorderStatus {

    private static final String TAG = "TrackRecorderStatus";
    private boolean isEverythingGoodForRecording = true;
    private int numOfTrackPoints;
    private TrackpointEntity previousSavedTrackpoint, actualSavedTrackpoint, candidateTrackpoint;
    private final LowPassFilter altitudeFilter;
    private final LowPassFilter speedFilter;
    private final AltitudeZeroFilter altitudeZeroFilter;
    private final RecordParameters recordParameters;

    @SuppressWarnings("WeakerAccess")
    @Inject
    SettingsRepository settingsRepository;

    public TrackRecorderStatus() {
        // MyLog.d(TAG, "TrackRecorderStatus");
        TrakrApplication.getInstance().getAppComponent().inject(this);

        recordParameters = settingsRepository.getRecordParameters();
        altitudeFilter = new LowPassFilter(recordParameters.getAltitudeFilterParameter());
        speedFilter = new LowPassFilter(recordParameters.getSpeedFilterParameter());
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
        } else {
            // if there was no previous points then the altitudes zero filter can not work
            // if the second points's altitudes is 0
            // therefore it must be initialized
            altitudeZeroFilter.setLastSavedTrackpoint(candidateTrackpoint);
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

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    boolean isEverythingGoodForRecording() {
        return isEverythingGoodForRecording;
    }

    @SuppressWarnings("SameParameterValue")
    void setEverythingGoodForRecording(boolean everythingGoodForRecording) {
        isEverythingGoodForRecording = everythingGoodForRecording;
    }

    int getTrackingDistance() {
        return recordParameters.getTrackingDistance();
    }

    int getTrackingAccuracy() {
        return recordParameters.getTrackingAccuracy();
    }

    int getTrackingInterval() {
        return recordParameters.getTrackingInterval();
    }

    int getMinimalRecordAccuracy() {
        return recordParameters.getMinimalRecordAccuracy();
    }

    int getAltitudeFilterParameter() {
        return recordParameters.getAltitudeFilterParameter();
    }

    int getSpeedFilterParameter() {
        return recordParameters.getSpeedFilterParameter();
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