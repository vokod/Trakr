package com.awolity.trakr.trackrecorder;

import com.awolity.trakr.data.entity.TrackpointEntity;

@SuppressWarnings("WeakerAccess")
public class AltitudeZeroFilter {

    private TrackpointEntity lastSavedTrackpoint;

    public void filterNext(TrackpointEntity candidateTrackpoint) {
        if (lastSavedTrackpoint != null && candidateTrackpoint.getUnfilteredAltitude() == 0) {
            candidateTrackpoint.setUnfilteredAltitude(lastSavedTrackpoint.getAltitude());
        }
        lastSavedTrackpoint = candidateTrackpoint;
    }

    public void setLastSavedTrackpoint(TrackpointEntity lastSavedTrackpoint){
        this.lastSavedTrackpoint = lastSavedTrackpoint;
    }
}
