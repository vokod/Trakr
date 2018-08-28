package com.awolity.trakr.repository;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.awolity.trakr.TrakrApplication;
import com.awolity.trakrutils.Constants;
import com.awolity.trakrutils.MyLog;
import com.awolity.trakrutils.RecordParameters;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class SettingsRepository {

    private static final String TAG = "SettingsRepository";

    private static final String KEY_LAST_RECORDED_TRACK_ID = "pref_key_last_recorded_track_id";
    private static final String KEY_LAST_ACTIVITY_TYPE = "pref_key_activity_type";
    private static final String KEY_USER_ID = "pref_key_user_id";

    public static final long NO_LAST_RECORDED_TRACK = -1;

    private static final String VALUE_USER_NOT_LOGGED_IN = "user_not_logged_in";

    private final static String KEY_ACCURACY = "key_accuracy";
    private final static int VALUE_ACCURACY_HIGH_ACCURACY = 2;
    private final static int VALUE_ACCURACY_BALANCED = 1;
    private final static int VALUE_ACCURACY_LOW_POWER = 0;

    private final static String KEY_UNIT = "key_unit";
    private final static int VALUE_UNIT_METRIC = 0;
    private final static int VALUE_UNIT_IMPERIAL = 1;

    @SuppressWarnings("WeakerAccess")
    @Inject
    Context context;

    private final SharedPreferences sharedPreferences;

    public SettingsRepository() {
        TrakrApplication.getInstance().getAppComponent().inject(this);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public int getAccuracy() {
        return sharedPreferences.getInt(KEY_ACCURACY, 2);
    }

    public void setAccuracy(int accuracy) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_ACCURACY, accuracy);
        editor.apply();
    }

    public RecordParameters getRecordParameters() {
        switch (getAccuracy()) {
            case Constants.ACCURACY_LOW_POWER:
                return Constants.RECORD_PARAMETERS_LOW_POWER;
            case Constants.ACCURACY_BALANCED:
                return Constants.RECORD_PARAMETERS_BALANCED;
            case Constants.ACCURACY_HIGH_ACCURACY:
                return Constants.RECORD_PARAMETERS_MOST_ACCURATE;
            default:
                return Constants.RECORD_PARAMETERS_BALANCED;
        }
    }

    public int getUnit() {
        MyLog.d(TAG, "getUnit");
        return sharedPreferences.getInt(KEY_UNIT, 0);
    }

    public void setUnit(int unit) {
        MyLog.d(TAG, "setUnit: " + unit);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_UNIT, unit);
        editor.apply();
    }

    public long getLastRecordedTrackId() {

        return sharedPreferences.getLong(KEY_LAST_RECORDED_TRACK_ID,
                Constants.NO_LAST_RECORDED_TRACK);
    }

    public  void setLastRecordedTrackId( long trackId) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(KEY_LAST_RECORDED_TRACK_ID, trackId);
        editor.apply();
    }
}
