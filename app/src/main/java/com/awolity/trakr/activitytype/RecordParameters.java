package com.awolity.trakr.activitytype;

public class RecordParameters {

    private final int trackingDistance, trackingAccuracy, trackingInterval, minimalRecordAccuracy,
            altitudeFilterParameter, speedFilterParameter;

    public RecordParameters(int trackingDistance, int trackingAccuracy, int trackingInterval,
                            int minimalRecordAccuracy, int altitudeFilterParameter,
                            int speedFilterParameter) {
        this.trackingDistance = trackingDistance;
        this.trackingAccuracy = trackingAccuracy;
        this.trackingInterval = trackingInterval;
        this.minimalRecordAccuracy = minimalRecordAccuracy;
        this.altitudeFilterParameter = altitudeFilterParameter;
        this.speedFilterParameter = speedFilterParameter;
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

    public int getMinimalRecordAccuracy() {
        return minimalRecordAccuracy;
    }

    public int getAltitudeFilterParameter() {
        return altitudeFilterParameter;
    }

    public int getSpeedFilterParameter() {
        return speedFilterParameter;
    }
}
