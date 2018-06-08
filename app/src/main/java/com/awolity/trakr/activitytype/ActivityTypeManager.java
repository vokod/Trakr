package com.awolity.trakr.activitytype;

import com.awolity.trakr.R;
import com.google.android.gms.location.LocationRequest;

import java.util.ArrayList;

public class ActivityTypeManager {

    private ArrayList<ActivityType> activityTypes;
    private static ActivityTypeManager instance;

    public static ActivityTypeManager getInstance() {
        if (instance == null) {
            instance = new ActivityTypeManager();
        }
        return instance;
    }

    private ActivityTypeManager() {
        activityTypes = createActivityTypes();
    }

    public ArrayList<ActivityType> getActivityTypes() {
        return activityTypes;
    }


    // TODO: this to a json file
    private ArrayList<ActivityType> createActivityTypes() {
        ArrayList<ActivityType> activityTypes = new ArrayList<>();

        RecordParameters walkingRecordParameters = new RecordParameters(1,
                LocationRequest.PRIORITY_HIGH_ACCURACY, 1, 5, 10);
        ActivityType walking = new ActivityType("Walking", R.drawable.ic_walk_circle, R.drawable.ic_walk, walkingRecordParameters);
        activityTypes.add(walking);

        RecordParameters runningRecordParameters = new RecordParameters(2,
                LocationRequest.PRIORITY_HIGH_ACCURACY, 1, 10, 10);
        ActivityType running = new ActivityType("Running", R.drawable.ic_run_circle, R.drawable.ic_run, runningRecordParameters);
        activityTypes.add(running);

        RecordParameters bikingRecordParameters = new RecordParameters(5,
                LocationRequest.PRIORITY_HIGH_ACCURACY, 2, 20, 10);
        ActivityType biking = new ActivityType("Biking", R.drawable.ic_bike_circle, R.drawable.ic_bike, bikingRecordParameters);
        activityTypes.add(biking);

        RecordParameters drivingRecordParameters = new RecordParameters(10,
                LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY, 5, 20, 10);
        ActivityType driving = new ActivityType("Driving", R.drawable.ic_car_circle, R.drawable.ic_car, drivingRecordParameters);
        activityTypes.add(driving);

        RecordParameters flyingRecordParameters = new RecordParameters(50,
                LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY, 5, 100, 10);
        ActivityType flying = new ActivityType("Flying", R.drawable.ic_plane_circle, R.drawable.ic_plane, drivingRecordParameters);
        activityTypes.add(flying);

        return activityTypes;
    }
}
