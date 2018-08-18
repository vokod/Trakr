package com.awolity.trakr.repository;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.awolity.trakr.TrakrApplication;
import com.awolity.trakr.trackrecorder.RecordParameters;
import com.awolity.trakr.utils.Constants;
import com.awolity.trakr.utils.MyLog;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class SettingsRepository {

    private static final String TAG = "SettingsRepository";
    private final static String KEY_ACCURACY = "key_accuracy";
    private final static int ACCURACY_HIGH_ACCURACY = 2;
    private final static int ACCURACY_BALANCED = 1;
    private final static int ACCURACY_LOW_POWER = 0;
    private final static String KEY_UNIT = "key_unit";
    private final static int UNIT_METRIC = 0;
    private final static int UNIT_IMPERIAL = 1;

    @Inject
    Context context;

    private SharedPreferences sharedPreferences;

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


}
