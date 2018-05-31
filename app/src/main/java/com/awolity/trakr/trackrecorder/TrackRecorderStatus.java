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
            altitudeFilterParameter;
    private TrackpointEntity previousTrackpoint, actualTrackpoint;
    private AltitudeFilter altitudeFilter;
// TODO: ha menetközben állítjuk a preferenciákat, akkor mi lesz? Ne lehessen állítani menetközben a preferenciákat

    void setupPreferences(Context context) {
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

    TrackpointEntity getActualTrackpoint() {
        return actualTrackpoint;
    }

    void setActualTrackpoint(TrackpointEntity actualTrackpoint) {
        if (this.actualTrackpoint != null) {
            previousTrackpoint = this.actualTrackpoint;
        }

        this.actualTrackpoint = actualTrackpoint;

        if (previousTrackpoint != null) {
            actualTrackpoint.setDistance(getGeologicalDistance(actualTrackpoint, previousTrackpoint));

            if (actualTrackpoint.getUnfilteredAltitude() == 0) {
                actualTrackpoint.setUnfilteredAltitude(previousTrackpoint.getAltitude());
            }

            // apply altitude filter
            if (isAltitudeFiltered) {
                actualTrackpoint.setAltitude(altitudeFilter.filterNext(
                        actualTrackpoint.getUnfilteredAltitude()));
            }
        }
    }

    boolean isDistanceFarEnoghFromPreviousTrackpoint() {
        return (int) actualTrackpoint.getDistance() > getTrackingDistance();
    }

    boolean isAccurateEnough() {
        return actualTrackpoint.getAccuracy() < getAccuracyFilterParameter();
    }

    boolean isThereAPreviousTrackpoint() {
        return previousTrackpoint != null;
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

    TrackpointEntity getPreviousTrackpoint() {
        return previousTrackpoint;
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