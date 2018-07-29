package com.awolity.trakr.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.awolity.trakr.R;
import com.awolity.trakr.activitytype.ActivityType;
import com.awolity.trakr.activitytype.ActivityTypeManager;

import java.util.List;
import java.util.UUID;

public class PreferenceUtils {

    public static long getLastRecordedTrackId(Context context) {
        SharedPreferences sharedpreferences =
                PreferenceManager.getDefaultSharedPreferences(context);
        return sharedpreferences.getLong(Constants.PREF_KEY_LAST_RECORDED_TRACK_ID,
                Constants.NO_LAST_RECORDED_TRACK);
    }

    public static void setLastRecordedTrackId(Context context, long trackId) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context)
                .edit();
        editor.putLong(Constants.PREF_KEY_LAST_RECORDED_TRACK_ID, trackId);
        editor.apply();
    }

    public static ActivityType getActivityType(Context context) {
        SharedPreferences sharedpreferences =
                PreferenceManager.getDefaultSharedPreferences(context);
        String name = sharedpreferences.getString(Constants.PREF_KEY_LAST_ACTIVITY_TYPE, "");
        List<ActivityType> activityTypeList = ActivityTypeManager.getInstance(context)
                .getActivityTypes();
        for (ActivityType activityType : activityTypeList) {
            if (activityType.getKey().equals(name)) {
                return activityType;
            }
        }
        return ActivityTypeManager.getInstance(context).getActivityTypes().get(0);
    }

    public static void setActivityType(Context context, ActivityType activityType) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context)
                .edit();
        editor.putString(Constants.PREF_KEY_LAST_ACTIVITY_TYPE, activityType.getKey());
        editor.apply();
    }
}

