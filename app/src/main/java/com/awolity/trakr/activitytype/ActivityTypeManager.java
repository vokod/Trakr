package com.awolity.trakr.activitytype;

import android.content.Context;

import com.awolity.trakr.R;
import com.google.android.gms.location.LocationRequest;

import java.util.ArrayList;

public class ActivityTypeManager {

    private ArrayList<ActivityType> activityTypes;
    private static ActivityTypeManager instance;

    public static ActivityTypeManager getInstance(Context context) {
        if (instance == null) {
            instance = new ActivityTypeManager(context);
        }
        return instance;
    }

    private ActivityTypeManager(Context context) {
        activityTypes = createActivityTypes(context);
    }

    public ArrayList<ActivityType> getActivityTypes() {
        return activityTypes;
    }

    private ArrayList<ActivityType> createActivityTypes(Context context) {
        ArrayList<ActivityType> activityTypes = new ArrayList<>();

        RecordParameters walkingRecordParameters = new RecordParameters(1,
                LocationRequest.PRIORITY_HIGH_ACCURACY, 1, 5, 10);
        ActivityType walking = new ActivityType(context.getString(R.string.activity_type_walking),
                context.getString(R.string.activity_type_key_walking),
                R.drawable.ic_walk_circle,
                R.drawable.ic_walk,
                walkingRecordParameters);
        activityTypes.add(walking);

        RecordParameters runningRecordParameters = new RecordParameters(2,
                LocationRequest.PRIORITY_HIGH_ACCURACY, 1, 10, 10);
        ActivityType running = new ActivityType(context.getString(R.string.activity_type_running),
                context.getString(R.string.activity_type_key_running),
                R.drawable.ic_run_circle,
                R.drawable.ic_run,
                runningRecordParameters);
        activityTypes.add(running);

        RecordParameters bikingRecordParameters = new RecordParameters(5,
                LocationRequest.PRIORITY_HIGH_ACCURACY, 2, 20, 10);
        ActivityType biking = new ActivityType(context.getString(R.string.activity_type_biking),
                context.getString(R.string.activity_type_key_biking),
                R.drawable.ic_bike_circle,
                R.drawable.ic_bike,
                bikingRecordParameters);
        activityTypes.add(biking);

        RecordParameters drivingRecordParameters = new RecordParameters(10,
                LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY, 5, 20, 10);
        ActivityType driving = new ActivityType(context.getString(R.string.activity_type_driving),
                context.getString(R.string.activity_type_key_driving),
                R.drawable.ic_car_circle,
                R.drawable.ic_car,
                drivingRecordParameters);
        activityTypes.add(driving);

        RecordParameters flyingRecordParameters = new RecordParameters(50,
                LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY, 5, 100, 10);
        ActivityType flying = new ActivityType(context.getString(R.string.activity_type_flying),
                context.getString(R.string.activity_type_key_flying),
                R.drawable.ic_plane_circle,
                R.drawable.ic_plane,
                drivingRecordParameters);
        activityTypes.add(flying);

        return activityTypes;
    }
}
