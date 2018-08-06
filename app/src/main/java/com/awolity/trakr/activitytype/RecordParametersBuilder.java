package com.awolity.trakr.activitytype;

public class RecordParametersBuilder {
    private int trackingDistance;
    private int trackingAccuracy;
    private int trackingInterval;
    private int accuracyFilterParameter;
    private int altitudeFilterParameter;
    private int speedFilterParameter;

    public RecordParametersBuilder setTrackingDistance(int trackingDistance) {
        this.trackingDistance = trackingDistance;
        return this;
    }

    public RecordParametersBuilder setTrackingAccuracy(int trackingAccuracy) {
        this.trackingAccuracy = trackingAccuracy;
        return this;
    }

    public RecordParametersBuilder setTrackingInterval(int trackingInterval) {
        this.trackingInterval = trackingInterval;
        return this;
    }

    public RecordParametersBuilder setMinimalRecordAccuracy(int accuracyFilterParameter) {
        this.accuracyFilterParameter = accuracyFilterParameter;
        return this;
    }

    public RecordParametersBuilder setAltitudeFilterParameter(int altitudeFilterParameter) {
        this.altitudeFilterParameter = altitudeFilterParameter;
        return this;
    }

    public RecordParametersBuilder setSpeedFilterParameter(int speedFilterParameter) {
        this.speedFilterParameter = speedFilterParameter;
        return this;
    }

    public RecordParameters build() {
        return new RecordParameters(trackingDistance, trackingAccuracy, trackingInterval,
                accuracyFilterParameter, altitudeFilterParameter, speedFilterParameter);
    }


}