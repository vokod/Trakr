package com.awolity.trakr.trackrecorder;

import android.content.Context;
import android.location.Location;

import com.awolity.trakr.data.entity.TrackpointEntity;
import com.awolity.trakr.track.AltitudeFilter;
import com.awolity.trakr.utils.PreferenceUtils;

class TrackRecorderStatus {

    private boolean
            isAltitudeFiltered, isEverythingGoodForRecording = true;
    private int trackingDistance, trackingAccuracy, trackingInterval, accuracyFilterParameter,
            altitudeFilterParameter, numOfTrackPoints;
    private TrackpointEntity previousSavedTrackpoint, actualSavedTrackpoint, candidateTrackpoint;
    private AltitudeFilter altitudeFilter;
// TODO: ha menetközben állítjuk a preferenciákat, akkor mi lesz? Ne lehessen állítani menetközben a preferenciákat

    public TrackRecorderStatus(Context context) {
        trackingDistance = (PreferenceUtils.getPreferenceTrackingDistance(context));
        trackingInterval = (PreferenceUtils.getPreferenceTrackingInterval(context));
        trackingAccuracy = (PreferenceUtils.getPreferenceGeolocationPriority(context));
        accuracyFilterParameter = (PreferenceUtils.getPreferenceAccuracyFilterParameter(context));
        isAltitudeFiltered = (PreferenceUtils.getPreferenceAltitudeFilter(context));
        altitudeFilterParameter = (PreferenceUtils.getPreferenceAltitudeFilterParameter(context));

        if (isAltitudeFiltered) {
            altitudeFilter = new AltitudeFilter(altitudeFilterParameter);
        }
    }

    TrackpointEntity getActualSavedTrackpoint() {
        return actualSavedTrackpoint;
    }

    void setCandidateTrackpoint(TrackpointEntity candidateTrackPoint) {
        this.candidateTrackpoint = candidateTrackPoint;

        if (actualSavedTrackpoint != null) {
            candidateTrackPoint.setDistance(getGeologicalDistance(candidateTrackPoint, actualSavedTrackpoint));

            // ha a jelölt magassága nulla, akkor legyen inkább az előző magassága
            if (candidateTrackPoint.getUnfilteredAltitude() == 0) {
                candidateTrackPoint.setUnfilteredAltitude(actualSavedTrackpoint.getAltitude());
            }

            // apply altitude filter
            if (isAltitudeFiltered) {
                candidateTrackPoint.setAltitude(altitudeFilter.filterNext(
                        candidateTrackPoint.getUnfilteredAltitude()));
            }
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

    int getNumOfTrackPoints(){
        return numOfTrackPoints;
    }

    TrackpointEntity getCandidateTrackpoint(){
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
}