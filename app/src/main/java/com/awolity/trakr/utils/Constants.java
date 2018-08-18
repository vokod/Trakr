package com.awolity.trakr.utils;

import com.awolity.trakr.trackrecorder.RecordParameters;
import com.google.android.gms.location.LocationRequest;

public class Constants {

    // preference keys
    public static final String PREF_KEY_LAST_RECORDED_TRACK_ID = "pref_key_last_recorded_track_id";
    public static final String PREF_KEY_LAST_ACTIVITY_TYPE = "pref_key_activity_type";
    public static final String PREF_KEY_USER_ID = "pref_key_user_id";
    public static final String PREF_VALUE_USER_NOT_LOGGED_IN = "user_not_logged_in";
    public static final long NO_LAST_RECORDED_TRACK = -1;

    // firebase realtime database nodes
    public static final String NODE_TRACKS = "tracks";
    public static final String NODE_TRACKPOINTS = "trackpoints";

    public static final int SIMPLIFIED_TRACK_POINT_MAX_NUMBER_FOR_DETAILS = 100;
    public static final int SIMPLIFIED_TRACK_POINT_MAX_NUMBER_FOR_LIST_ITEMS = 50;

    public static final RecordParameters RECORD_PARAMETERS_MOST_ACCURATE = new RecordParameters(
            3,
            2,
            LocationRequest.PRIORITY_HIGH_ACCURACY,
            20,
            10,
            2);

    public static final RecordParameters RECORD_PARAMETERS_BALANCED = new RecordParameters(
            10,
            5,
            LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY,
            50,
            10,
            2);

    public static final RecordParameters RECORD_PARAMETERS_LOW_POWER = new RecordParameters(
            25,
            10,
            LocationRequest.PRIORITY_LOW_POWER,
            100,
            10,
            2);
}
