package com.awolity.trakr.utils;

import com.google.android.gms.location.LocationRequest;

public class Constants {

    // firebase realtime database nodes
    public static final String COLLECTION_TRACKS = "tracks";
    public static final String COLLECTION_USERS = "users";
    public static final String DOCUMENT_GEOPOINTS = "pg";
    public static final String DOCUMENT_DISTANCES = "pd";
    public static final String DOCUMENT_ALTITUDES = "pa";
    public static final String DOCUMENT_SPEEDS = "pv";
    public static final String DOCUMENT_TIMES = "pt";
    public static final String COLLECTION_POINTS = "points";

    // public static final int MAP_POINT_MAX_NUMBER_FOR_EXPLORE = 200;
    public static final int MAP_POINT_MAX_NUMBER_FOR_TRACK_LIST = 50;
    public static final int CHART_POINT_MAX_NUMBER_FOR_TRACK_DETAIL = 300;
    public static final int CHART_POINT_MAX_NUMBER_FOR_BOTTOM_SHEET_CHARTS_FRAGMENT = 100;

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
            LocationRequest.PRIORITY_HIGH_ACCURACY,
            50,
            6,
            2);

    public static final RecordParameters RECORD_PARAMETERS_LOW_POWER = new RecordParameters(
            25,
            10,
            LocationRequest.PRIORITY_HIGH_ACCURACY,
            100,
            3,
            1);

    // preference values
    public static final long NO_LAST_RECORDED_TRACK = -1;
    public final static int ACCURACY_HIGH_ACCURACY = 2;
    public final static int ACCURACY_BALANCED = 1;
    public final static int ACCURACY_LOW_POWER = 0;
    public final static int ACCURACY_MAX_VALUE = ACCURACY_HIGH_ACCURACY;
    public final static int UNIT_METRIC = 0;
    public final static int UNIT_IMPERIAL = 1;

    public final static double MILE = 1.609344;
    public final static double FOOT = 0.3048;

    public final static String STRING_DEFAULT_VALUE = "";
}
