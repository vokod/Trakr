package com.awolity.trakr.utils;

import android.os.Parcel;
import android.os.Parcelable;

public class RecordParameters implements Parcelable {

    private final int trackingDistance, trackingAccuracy, trackingInterval, minimalRecordAccuracy,
            altitudeFilterParameter, speedFilterParameter;

    RecordParameters(int trackingDistance, int trackingInterval, int trackingAccuracy,
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.trackingDistance);
        dest.writeInt(this.trackingAccuracy);
        dest.writeInt(this.trackingInterval);
        dest.writeInt(this.minimalRecordAccuracy);
        dest.writeInt(this.altitudeFilterParameter);
        dest.writeInt(this.speedFilterParameter);
    }

    @SuppressWarnings("WeakerAccess")
    protected RecordParameters(Parcel in) {
        this.trackingDistance = in.readInt();
        this.trackingAccuracy = in.readInt();
        this.trackingInterval = in.readInt();
        this.minimalRecordAccuracy = in.readInt();
        this.altitudeFilterParameter = in.readInt();
        this.speedFilterParameter = in.readInt();
    }

    public static final Parcelable.Creator<RecordParameters> CREATOR = new Parcelable.Creator<RecordParameters>() {
        @Override
        public RecordParameters createFromParcel(Parcel source) {
            return new RecordParameters(source);
        }

        @Override
        public RecordParameters[] newArray(int size) {
            return new RecordParameters[size];
        }
    };
}