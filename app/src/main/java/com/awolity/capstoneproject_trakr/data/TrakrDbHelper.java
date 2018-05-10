package com.awolity.capstoneproject_trakr.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.awolity.capstoneproject_trakr.data.TrakrContract.TrackEntry;
import com.awolity.capstoneproject_trakr.data.TrakrContract.TrackpointEntry;

class TrakrDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 3;

    private static final String DATABASE_NAME = "trakr.db";

    public TrakrDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_TRACK_TABLE = "CREATE TABLE " + TrackEntry.TABLE_NAME + " (" +
                TrackEntry._ID + " INTEGER PRIMARY KEY," +
                TrackEntry.COL_TITLE + " TEXT NOT NULL, " +
                TrackEntry.COL_START_TIME + " INTEGER NOT NULL, " +
                TrackEntry.COL_DISTANCE + " REAL NOT NULL, " +
                TrackEntry.COL_ASCENT + " INTEGER NOT NULL, " +
                TrackEntry.COL_DESCENT + " INTEGER NOT NULL, " +
                TrackEntry.COL_ELAPSED_TIME + " INTEGER NOT NULL, " +
                TrackEntry.COL_TRACK_POINT_NUM + " INTEGER NOT NULL, " +
                TrackEntry.COL_NORTH + " REAL NOT NULL," +
                TrackEntry.COL_SOUTH + " REAL NOT NULL, " +
                TrackEntry.COL_EAST + " REAL NOT NULL, " +
                TrackEntry.COL_WEST + " REAL NOT NULL, " +
                TrackEntry.COL_HIGH + " REAL NOT NULL, " +
                TrackEntry.COL_LOW + " REAL NOT NULL, " +
                TrackEntry.COL_MAX_SPEED + " REAL NOT NULL, " +
                TrackEntry.COL_AVG_SPEED + " REAL NOT NULL, " +
                TrackEntry.COL_IS_SAVED + " INTEGER NOT NULL, " +
                TrackEntry.COL_METADATA + " TEXT " +
                " );";

        final String SQL_CREATE_TRACKPOINT_TABLE = "CREATE TABLE " + TrackpointEntry.TABLE_NAME + " (" +

                TrackpointEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +

                // the ID of the track entry associated with this trackpoint data
                TrackpointEntry.COL_TRACK_ID + " INTEGER NOT NULL, " +
                TrackpointEntry.COL_TIME + " INTEGER NOT NULL, " +
                TrackpointEntry.COL_LATITUDE + " REAL NOT NULL, " +
                TrackpointEntry.COL_LONGITUDE + " REAL NOT NULL," +
                TrackpointEntry.COL_ALTITUDE + " REAL NOT NULL, " +
                TrackpointEntry.COL_BEARING + " REAL NOT NULL, " +
                TrackpointEntry.COL_SPEED + " REAL NOT NULL, " +
                TrackpointEntry.COL_ACCURACY + " REAL NOT NULL, " +
                TrackpointEntry.COL_DISTANCE + " REAL NOT NULL, " +
                TrackpointEntry.COL_ALTITUDE_UNFILTERED + " REAL NOT NULL, " +
                // Set up the track_id column as a foreign key to track table.
                " FOREIGN KEY (" + TrackpointEntry.COL_TRACK_ID + ") REFERENCES " +
                TrackEntry.TABLE_NAME + " (" + TrackEntry._ID + ") " +
                " );";

        sqLiteDatabase.execSQL(SQL_CREATE_TRACK_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_TRACKPOINT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // for the time being, the update strategy is to dump th old tables.
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TrackEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TrackpointEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
