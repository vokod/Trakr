package com.awolity.trakr.utils;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.awolity.trakr.R;
import com.awolity.trakr.activitytype.ActivityType;
import com.awolity.trakr.activitytype.ActivityTypeManager;

import java.util.List;

public class PreferenceUtils {

    public static final long NO_LAST_RECORDED_TRACK = -1;

    public static long getLastRecordedTrackId(Context context) {
        SharedPreferences sharedpreferences =
                PreferenceManager.getDefaultSharedPreferences(context);
        return sharedpreferences.getLong(context.getString(R.string.pref_key_last_recorded_track_id),
                NO_LAST_RECORDED_TRACK);
    }

    public static void setLastRecordedTrackId(Context context, long trackId) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putLong(context.getString(R.string.pref_key_last_recorded_track_id), trackId);
        editor.apply();
    }

    public static ActivityType getActivityType(Context context) {
        SharedPreferences sharedpreferences =
                PreferenceManager.getDefaultSharedPreferences(context);
        String name = sharedpreferences.getString(context.getString(R.string.pref_key_activity_type),"");
        List<ActivityType> activityTypeList = ActivityTypeManager.getInstance(context).getActivityTypes();
        for(ActivityType activityType : activityTypeList){
            if(activityType.getKey().equals(name)){
                return activityType;
            }
        }
        return null;
    }

    public static void setActivityType(Context context, ActivityType activityType) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putString(context.getString(R.string.pref_key_activity_type), activityType.getKey());
        editor.apply();
    }
}

