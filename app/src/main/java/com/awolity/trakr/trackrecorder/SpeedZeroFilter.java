package com.awolity.trakr.trackrecorder;

import com.awolity.trakr.data.entity.TrackpointEntity;
import com.awolity.trakr.utils.MyLog;

public class SpeedZeroFilter {

    private static final String TAG = "SpeedZeroFilter";
    private TrackpointEntity lastSavedTrackpoint;
    private boolean speedZeroFilterAlreadyApplied;

    public SpeedZeroFilter() {
    }

    public void filterNext(TrackpointEntity candidateTrackpoint) {
        MyLog.d(TAG, "filterNext");
        if (lastSavedTrackpoint == null) {
            lastSavedTrackpoint = candidateTrackpoint;
            return;
        }

        // apply speed zero filter: if candidate trackpoint speed is 0,
        // then set it to previous point's speed.
        // but only do it once.
        if (candidateTrackpoint.getSpeed() == 0 && !speedZeroFilterAlreadyApplied) {
            MyLog.d(TAG, "filterNext - actual speed is 0, filter not applied to the " +
                    "previous point, applying now");
            speedZeroFilterAlreadyApplied = true;
            candidateTrackpoint.setSpeed(lastSavedTrackpoint.getSpeed());
        } else if (candidateTrackpoint.getSpeed() == 0 && speedZeroFilterAlreadyApplied) {
            MyLog.d(TAG, "filterNext - actual speed is 0, filter  applied to the previous " +
                    "point, NOT applying again.");
            speedZeroFilterAlreadyApplied = false;
        }

        lastSavedTrackpoint = candidateTrackpoint;
    }
}
