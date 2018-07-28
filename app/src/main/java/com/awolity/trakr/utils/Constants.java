package com.awolity.trakr.utils;

public class Constants {

    // preference keys
    public static final String PREF_KEY_LAST_RECORDED_TRACK_ID = "pref_key_last_recorded_track_id";
    public static final String PREF_KEY_LAST_ACTIVITY_TYPE = "pref_key_activity_type";
    public static final String PREF_KEY_LAST_INSTALLATION_ID = "pref_key_installation_id";
    public static final long NO_LAST_RECORDED_TRACK = -1;

    // firebase realtime database nodes
    public static final String NODE_TRACKS = "tracks";
    public static final String NODE_TRACKPOINTS = "trackpoints";
    public static final String NODE_INSTALLATIONS = "installations";
    public static final String NODE_DELETED_TRACKS = "deleted_tracks";


}
