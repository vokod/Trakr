package com.awolity.trakrutils;

import com.google.android.gms.location.LocationRequest;

public class Constants {

    // preference keys
    public static final long NO_LAST_RECORDED_TRACK = -1;

    // firebase realtime database nodes
    public static final String NODE_TRACKS = "tracks";
    public static final String NODE_TRACKPOINTS = "trackpoints";
    public static final String USER_TO_DELETE = "user_to_delete";

    public static final int SIMPLIFIED_TRACK_POINT_MAX_NUMBER_FOR_DETAILS = 100;
    public static final int SIMPLIFIED_TRACK_POINT_MAX_NUMBER_FOR_LIST_ITEMS = 50;

    public static final RecordParameters RECORD_PARAMETERS_MOST_ACCURATE = new RecordParameters(
            3,
            2,
            LocationRequest.PRIORITY_HIGH_ACCURACY,
            20,
            10,
            3);

    public static final RecordParameters RECORD_PARAMETERS_BALANCED = new RecordParameters(
            10,
            5,
            LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY,
            50,
            6,
            2);

    public static final RecordParameters RECORD_PARAMETERS_LOW_POWER = new RecordParameters(
            25,
            10,
            LocationRequest.PRIORITY_LOW_POWER,
            100,
            3,
            1);

    public final static int ACCURACY_HIGH_ACCURACY = 2;
    public final static int ACCURACY_BALANCED = 1;
    public final static int ACCURACY_LOW_POWER = 0;
    public final static int UNIT_METRIC = 0;
    public final static int UNIT_IMPERIAL = 1;

}
