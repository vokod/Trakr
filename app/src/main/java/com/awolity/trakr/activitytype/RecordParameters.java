package com.awolity.trakr.activitytype;

public class RecordParameters {

    private final int trackingDistance, trackingAccuracy, trackingInterval, accuracyFilterParameter,
            altitudeFilterParameter;

    public RecordParameters(int trackingDistance, int trackingAccuracy, int trackingInterval,
                            int accuracyFilterParameter, int altitudeFilterParameter) {
        this.trackingDistance = trackingDistance;
        this.trackingAccuracy = trackingAccuracy;
        this.trackingInterval = trackingInterval;
        this.accuracyFilterParameter = accuracyFilterParameter;
        this.altitudeFilterParameter = altitudeFilterParameter;
    }

    public int getTrackingDistance() {
        return trackingDistance;
    }

    public int getTrackingAccuracy() {
        return trackingAccuracy;
    }

    public int getTrackingInterval() {
        return trackingInterval;
    }

    public int getAccuracyFilterParameter() {
        return accuracyFilterParameter;
    }

    public int getAltitudeFilterParameter() {
        return altitudeFilterParameter;
    }
}
