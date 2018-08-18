package com.awolity.trakr.repository;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.awolity.trakr.TrakrApplication;
import com.awolity.trakr.trackrecorder.RecordParameters;
import com.awolity.trakr.utils.Constants;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class SettingsRepository {

    @Inject
    Context context;

    private final static String KEY_ACCURACY = "key_accuracy";
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
            case 0:
                return Constants.RECORD_PARAMETERS_LOW_POWER;
            case 1:
                return Constants.RECORD_PARAMETERS_BALANCED;
            case 2:
                return Constants.RECORD_PARAMETERS_MOST_ACCURATE;
            default:
                return Constants.RECORD_PARAMETERS_BALANCED;
        }
    }
}
