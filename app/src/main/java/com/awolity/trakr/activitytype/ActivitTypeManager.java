package com.awolity.trakr.activitytype;

import com.awolity.trakr.R;
import com.google.android.gms.location.LocationRequest;

import java.util.ArrayList;

public class ActivitTypeManager {

    public static ArrayList<ActivityType> getActivityTypes() {
        ArrayList<ActivityType> activityTypes = new ArrayList<>();

        RecordParameters walkingRecordParameters = new RecordParameters(1,
                LocationRequest.PRIORITY_HIGH_ACCURACY, 1, 5, 10);
        ActivityType walking = new ActivityType("Walking", R.drawable.ic_walk, walkingRecordParameters);
        activityTypes.add(walking);

        RecordParameters runningRecordParameters = new RecordParameters(2,
                LocationRequest.PRIORITY_HIGH_ACCURACY, 1, 10, 10);
        ActivityType running = new ActivityType("Running", R.drawable.ic_run, runningRecordParameters);
        activityTypes.add(running);

        RecordParameters bikingRecordParameters = new RecordParameters(5,
                LocationRequest.PRIORITY_HIGH_ACCURACY, 2, 20, 10);
        ActivityType biking = new ActivityType("Running", R.drawable.ic_bike, bikingRecordParameters);
        activityTypes.add(biking);

        RecordParameters drivingRecordParameters = new RecordParameters(10,
                LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY, 5, 20, 10);
        ActivityType driving = new ActivityType("Driving", R.drawable.ic_car, drivingRecordParameters);
        activityTypes.add(driving);

        RecordParameters flyingRecordParameters = new RecordParameters(50,
                LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY, 5, 100, 10);
        ActivityType flying = new ActivityType("Flying", R.drawable.ic_plane, drivingRecordParameters);
        activityTypes.add(flying);

        return activityTypes;
    }
}
