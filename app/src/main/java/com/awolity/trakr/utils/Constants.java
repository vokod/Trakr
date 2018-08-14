package com.awolity.trakr.utils;

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

    public static final int SIMPLIFIED_TRACK_POINT_MAX_NUMBER_FOR_CHARTS = 500;
    public static final int SIMPLIFIED_TRACK_POINT_MAX_NUMBER_FOR_LIST_ITEM_POLYLINES = 50;


}
