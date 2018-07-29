package com.awolity.trakr.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.location.Location;

import com.awolity.trakr.location.LocationManager;
import com.awolity.trakr.utils.MyLog;
import com.google.android.gms.location.LocationRequest;

public class LocationViewModel extends AndroidViewModel implements LocationManager.LocationManagerCallback {

    private static final String LOG_TAG = LocationViewModel.class.getSimpleName();
    private final LocationManager locationManager;
    private MutableLiveData<Location> lastLocation;
    private boolean isLocationUpdating;

    public LocationViewModel(Application application) {
        super(application);
            locationManager = new LocationManager(2, 1, LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    public void isLocationSettingsGood(LocationManager.LocationSettingsCallback callback) {
        locationManager.isLocationSettingsGood(callback);
    }

    public LiveData<Location> getLocation() {
        MyLog.d(LOG_TAG, "getLocation");
        isLocationUpdating = true;
        if (lastLocation == null) {
            lastLocation = new MutableLiveData<>();
            locationManager.start(this);
        }
        return lastLocation;
    }

    public void stopLocation() {
        //MyLog.d(TAG, "stopLocation");
        if (isLocationUpdating) {
            locationManager.stop();
            lastLocation = null;
        }
        isLocationUpdating = false;
    }

    @Override
    public void onLocationChanged(Location location) {
        MyLog.d(LOG_TAG, "onLocationChanged");
        lastLocation.setValue(location);
    }
}