package com.awolity.trakr.trackrecorder;

import com.awolity.trakr.data.entity.TrackpointEntity;

@SuppressWarnings("WeakerAccess")
public class AltitudeZeroFilter {

    private static final String TAG = "AltitudeZeroFilter";
    private TrackpointEntity lastSavedTrackpoint;

    public AltitudeZeroFilter() {
    }

    public void filterNext(TrackpointEntity candidateTrackpoint) {
        // MyLog.d(TAG, "filterNext");

        if (lastSavedTrackpoint != null && candidateTrackpoint.getUnfilteredAltitude() == 0) {
            // MyLog.d(TAG, "filterNext - candidateTrackpoint altitude == 0, applying zero " +
            //        "filter. Altitude of previous point: " + lastSavedTrackpoint.getAltitude());
            candidateTrackpoint.setUnfilteredAltitude(lastSavedTrackpoint.getAltitude());
        }
        lastSavedTrackpoint = candidateTrackpoint;
    }
}
