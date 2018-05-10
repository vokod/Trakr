package com.awolity.trakr.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public class TrakrContract {

    public static final String CONTENT_AUTHORITY = "com.awolity.trakr.app";

    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_TRACK = "track";
    public static final String PATH_TRACKPOINT = "trackpoint";

    public static final class TrackpointEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_TRACKPOINT).build();

        public static final String CONTENT_DIR_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TRACKPOINT;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TRACKPOINT;

        public static final String TABLE_NAME = "trackpoint";
        public static final String COL_TRACK_ID = "track_id";
        public static final String COL_TIME = "time";
        public static final String COL_LATITUDE = "latitude";
        public static final String COL_LONGITUDE = "longitude";
        public static final String COL_ALTITUDE = "altitude";
        public static final String COL_ALTITUDE_UNFILTERED = "altitude_unfiltered";
        public static final String COL_BEARING = "bearing";
        public static final String COL_SPEED = "speed";
        public static final String COL_ACCURACY = "accuracy";
        public static final String COL_DISTANCE = "distance";

        public static Uri buildTrackpointUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static String getTrackIdFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }

    public static final class TrackEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_TRACK).build();

        public static final String CONTENT_DIR_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TRACK;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TRACK;

        public static final String TABLE_NAME = "track";
        public static final String COL_TITLE = "name";
        public static final String COL_START_TIME = "date";
        public static final String COL_DISTANCE = "length";
        public static final String COL_ASCENT = "ascent";
        public static final String COL_DESCENT = "descent";
        public static final String COL_ELAPSED_TIME = "elapsed_time";
        public static final String COL_TRACK_POINT_NUM = "track_point_num";
        public static final String COL_NORTH = "north_point";
        public static final String COL_SOUTH = "south_point";
        public static final String COL_EAST = "east_point";
        public static final String COL_WEST = "west_point";
        public static final String COL_HIGH = "high_point";
        public static final String COL_LOW = "low_point";
        public static final String COL_MAX_SPEED = "max_speed";
        public static final String COL_AVG_SPEED = "avg_speed";
        public static final String COL_IS_SAVED = "is_saved";
        public static final String COL_METADATA = "metadata";

        public static Uri buildTrackUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}
