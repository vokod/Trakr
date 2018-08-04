package com.awolity.trakr.trackrecorder;

import com.awolity.trakr.data.entity.TrackpointEntity;
import com.awolity.trakr.utils.MyLog;

public class AltitudeZeroFilter {

    private static final String TAG = "AltitudeZeroFilter";
    private TrackpointEntity lastSavedTrackpoint;

    public AltitudeZeroFilter() {
    }

    public void filterNext(TrackpointEntity candidateTrackpoint) {
        // MyLog.d(TAG, "filterNext");

        if (lastSavedTrackpoint == null && candidateTrackpoint.getUnfilteredAltitude() == 0) {
            // MyLog.d(TAG, "filterNext - candidateTrackpoint altitude == 0, applying zero " +
            //        "filter. Altitude of previous point: " + lastSavedTrackpoint.getAltitude());
            candidateTrackpoint.setUnfilteredAltitude(lastSavedTrackpoint.getAltitude());
        } else if(lastSavedTrackpoint == null ){
            // MyLog.d(TAG, "filterNext - there is no previous point to get the previous " +
            //        "altitude from filter. Altitude of previous point: ");
        }

        lastSavedTrackpoint = candidateTrackpoint;
    }
}
