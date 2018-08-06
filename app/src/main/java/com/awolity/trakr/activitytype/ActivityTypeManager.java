package com.awolity.trakr.activitytype;

import android.content.Context;

import com.awolity.trakr.R;
import com.google.android.gms.location.LocationRequest;

import java.util.ArrayList;

public class ActivityTypeManager {

    private static ArrayList<ActivityType> activityTypes;
    private static ActivityTypeManager instance;
    private static ActivityType walking, running, biking, driving, flying;

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

        RecordParameters walkingRecordParameters = new RecordParametersBuilder()
                .setTrackingAccuracy(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setTrackingDistance(1)
                .setTrackingInterval(1)
                .setMinimalRecordAccuracy(20)
                .setAltitudeFilterParameter(10)
                .setSpeedFilterParameter(2)
                .build();

        walking = new ActivityType(context.getString(R.string.activity_type_walking),
                context.getString(R.string.activity_type_key_walking),
                R.drawable.ic_walk_circle,
                R.drawable.ic_walk,
                walkingRecordParameters);
        activityTypes.add(walking);

        RecordParameters runningRecordParameters = new RecordParametersBuilder()
                .setTrackingAccuracy(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setTrackingDistance(2)
                .setTrackingInterval(2)
                .setMinimalRecordAccuracy(20)
                .setAltitudeFilterParameter(10)
                .setSpeedFilterParameter(2)
                .build();

        running = new ActivityType(context.getString(R.string.activity_type_running),
                context.getString(R.string.activity_type_key_running),
                R.drawable.ic_run_circle,
                R.drawable.ic_run,
                runningRecordParameters);
        activityTypes.add(running);

        RecordParameters bikingRecordParameters = new RecordParametersBuilder()
                .setTrackingAccuracy(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setTrackingDistance(5)
                .setTrackingInterval(3)
                .setMinimalRecordAccuracy(20)
                .setAltitudeFilterParameter(10)
                .setSpeedFilterParameter(2)
                .build();

        biking = new ActivityType(context.getString(R.string.activity_type_biking),
                context.getString(R.string.activity_type_key_biking),
                R.drawable.ic_bike_circle,
                R.drawable.ic_bike,
                bikingRecordParameters);
        activityTypes.add(biking);

        RecordParameters drivingRecordParameters = new RecordParametersBuilder()
                .setTrackingAccuracy(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setTrackingDistance(10)
                .setTrackingInterval(5)
                .setMinimalRecordAccuracy(40)
                .setAltitudeFilterParameter(10)
                .setSpeedFilterParameter(2)
                .build();

        driving = new ActivityType(context.getString(R.string.activity_type_driving),
                context.getString(R.string.activity_type_key_driving),
                R.drawable.ic_car_circle,
                R.drawable.ic_car,
                drivingRecordParameters);
        activityTypes.add(driving);

        RecordParameters flyingRecordParameters = new RecordParametersBuilder()
                .setTrackingAccuracy(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)
                .setTrackingDistance(50)
                .setTrackingInterval(5)
                .setMinimalRecordAccuracy(100)
                .setAltitudeFilterParameter(10)
                .setSpeedFilterParameter(2)
                .build();

        flying = new ActivityType(context.getString(R.string.activity_type_flying),
                context.getString(R.string.activity_type_key_flying),
                R.drawable.ic_plane_circle,
                R.drawable.ic_plane,
                flyingRecordParameters);
        activityTypes.add(flying);

        return activityTypes;
    }

    public ActivityType getActivityType(Context context, String key) {
        if (key == null) {
            return walking;
        }
        if (key.equals(context.getString(R.string.activity_type_key_walking))) {
            return walking;
        } else if (key.equals(context.getString(R.string.activity_type_key_running))) {
            return running;
        } else if (key.equals(context.getString(R.string.activity_type_key_biking))) {
            return biking;
        } else if (key.equals(context.getString(R.string.activity_type_key_driving))) {
            return driving;
        } else if (key.equals(context.getString(R.string.activity_type_key_flying))) {
            return flying;
        } else {
            return walking;
        }
    }
}
