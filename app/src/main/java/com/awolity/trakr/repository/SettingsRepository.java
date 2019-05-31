package com.awolity.trakr.repository;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.awolity.trakr.TrakrApplication;
import com.awolity.trakr.utils.Constants;
import com.awolity.trakr.utils.RecordParameters;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class SettingsRepository {

    private static final String KEY_LAST_RECORDED_TRACK_ID = "pref_key_last_recorded_track_id";
    private final static String KEY_ACCURACY = "key_accuracy";

    private final static String KEY_UNIT = "key_unit";

    @SuppressWarnings("WeakerAccess")
    @Inject
    Context context;

    private final SharedPreferences sharedPreferences;

    public SettingsRepository() {
        TrakrApplication.getInstance().getAppComponent().inject(this);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public int getAccuracy() {
        return sharedPreferences.getInt(KEY_ACCURACY, Constants.ACCURACY_MAX_VALUE);
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
        // MyLog.d(TAG, "getUnit");
        return sharedPreferences.getInt(KEY_UNIT, Constants.UNIT_METRIC);
    }

    public void setUnit(int unit) {
        // MyLog.d(TAG, "setUnit: " + unit);
        if (unit != Constants.UNIT_IMPERIAL && unit != Constants.UNIT_METRIC) {
            unit = Constants.UNIT_METRIC;
        }
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_UNIT, unit);
        editor.apply();
    }

    public long getLastRecordedTrackId() {

        return sharedPreferences.getLong(KEY_LAST_RECORDED_TRACK_ID,
                Constants.NO_LAST_RECORDED_TRACK);
    }

    public void setLastRecordedTrackId(long trackId) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(KEY_LAST_RECORDED_TRACK_ID, trackId);
        editor.apply();
    }
}
