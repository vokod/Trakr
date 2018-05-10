package com.awolity.capstoneproject_trakr.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

public class TrackProvider extends ContentProvider {

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private TrakrDbHelper mOpenHelper;

    private static final int TRACK = 100;
    private static final int TRACK_ID = 101;
    private static final int TRACKPOINT = 200;
    private static final int TRACKPOINTS_OF_TRACK = 201;
    private static final int TRACKPOINT_ID = 202;

    private static final SQLiteQueryBuilder sTrackpointsOfTrackQueryBuilder;

    static {
        sTrackpointsOfTrackQueryBuilder = new SQLiteQueryBuilder();

        //This is an inner join which looks like
        //Track INNER JOIN Track ON Trackpoint.track = track_id
        sTrackpointsOfTrackQueryBuilder.setTables(
                TrakrContract.TrackpointEntry.TABLE_NAME + " INNER JOIN " +
                        TrakrContract.TrackEntry.TABLE_NAME +
                        " ON " + TrakrContract.TrackpointEntry.TABLE_NAME +
                        "." + TrakrContract.TrackpointEntry.COL_TRACK_ID +
                        " = " + TrakrContract.TrackEntry.TABLE_NAME +
                        "." + TrakrContract.TrackEntry._ID);
    }

    private static final String sTrackIdSelection =
            TrakrContract.TrackEntry.TABLE_NAME +
                    "." + TrakrContract.TrackEntry._ID + " = ? ";

    private static UriMatcher buildUriMatcher() {
        // 1) The code passed into the constructor represents the code to return for the root
        // URI.  It's common to use NO_MATCH as the code for this case. Add the constructor below.
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = TrakrContract.CONTENT_AUTHORITY;

        // 2) Use the addURI function to match each of the types.  Use the constants from
        // MovieContract to help define the types to the UriMatcher.
        matcher.addURI(authority, TrakrContract.PATH_TRACK, TRACK);
        matcher.addURI(authority, TrakrContract.PATH_TRACK + "/#", TRACK_ID);
        matcher.addURI(authority, TrakrContract.PATH_TRACKPOINT, TRACKPOINT);
        matcher.addURI(authority, TrakrContract.PATH_TRACKPOINT + "/#", TRACKPOINTS_OF_TRACK);

        // 3) Return the new matcher!
        return matcher;
    }

    private Cursor getTrackpointsOfTrack(Uri uri, String[] projection, String sortOrder) {
        String trackString = TrakrContract.TrackpointEntry.getTrackIdFromUri(uri);

        String[] selectionArgs;
        String selection;

        selection = sTrackIdSelection;
        selectionArgs = new String[]{trackString};

        return sTrackpointsOfTrackQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new TrakrDbHelper(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case TRACK:
                return TrakrContract.TrackEntry.CONTENT_DIR_TYPE;
            case TRACK_ID:
                return TrakrContract.TrackEntry.CONTENT_ITEM_TYPE;
            case TRACKPOINT:
                return TrakrContract.TrackpointEntry.CONTENT_DIR_TYPE;
            case TRACKPOINTS_OF_TRACK:
                return TrakrContract.TrackpointEntry.CONTENT_DIR_TYPE;
            case TRACKPOINT_ID:
                return TrakrContract.TrackpointEntry.CONTENT_ITEM_TYPE;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        Cursor retCursor;

        switch (sUriMatcher.match(uri)) {
            case TRACK: {
                retCursor = mOpenHelper.getReadableDatabase().query(TrakrContract.TrackEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }
            case TRACKPOINT: {
                retCursor = mOpenHelper.getReadableDatabase().query(TrakrContract.TrackpointEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }
            case TRACK_ID: {
                retCursor = mOpenHelper.getReadableDatabase().query(TrakrContract.TrackEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }
            case TRACKPOINT_ID: {
                retCursor = mOpenHelper.getReadableDatabase().query(TrakrContract.TrackpointEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }
            case TRACKPOINTS_OF_TRACK: {
                retCursor = getTrackpointsOfTrack(uri, projection, sortOrder);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case TRACK: {
                long _id = db.insert(TrakrContract.TrackEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = TrakrContract.TrackEntry.buildTrackUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case TRACKPOINT: {
                long _id = db.insert(TrakrContract.TrackpointEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = TrakrContract.TrackpointEntry.buildTrackpointUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        final int match = sUriMatcher.match(uri);
        int rowsDeleted;

        if (selection == null) selection = "1";

        switch (match) {
            case TRACK: {
                rowsDeleted = db.delete(TrakrContract.TrackEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            case TRACKPOINT: {
                rowsDeleted = db.delete(TrakrContract.TrackpointEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(
            Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        if (selection == null) selection = "1";

        switch (match) {
            case TRACK: {
                rowsUpdated = db.update(TrakrContract.TrackEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            }
            case TRACKPOINT: {
                rowsUpdated = db.update(TrakrContract.TrackpointEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case TRACK: {
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(TrakrContract.TrackEntry.TABLE_NAME, null,
                                value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            }
            case TRACKPOINT: {
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(TrakrContract.TrackpointEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            }

            default:
                return super.bulkInsert(uri, values);
        }
    }
}
