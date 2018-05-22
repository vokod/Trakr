package com.awolity.trakr.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.location.Location;
import android.util.Log;

import com.awolity.trakr.location.LocationManager;
import com.awolity.trakr.utils.MyLog;
import com.awolity.trakr.utils.PreferenceUtils;

public class LocationViewModel extends AndroidViewModel implements LocationManager.LocationManagerCallback {

    private static final String TAG = LocationViewModel.class.getSimpleName();
    private LocationManager locationManager;
    private MutableLiveData<Location> lastLocation;
    private boolean isLocationUpdating;

    public LocationViewModel(Application application) {
        super(application);
        locationManager = new LocationManager(
                PreferenceUtils.getPreferenceTrackingInterval(application),
                PreferenceUtils.getPreferenceTrackingInterval(application) / 2,
                PreferenceUtils.getPreferenceGeolocationPriority(application));
    }

    public void isLocationSettingsGood(LocationManager.LocationSettingsCallback callback) {
        locationManager.isLocationSettingsGood(callback);
    }

    public LiveData<Location> getLocation() {
         MyLog.d(TAG, "getLocation");
        isLocationUpdating = true;
        if (lastLocation == null) {
            lastLocation = new MutableLiveData<>();
            locationManager.start(this);
        }
        return lastLocation;
    }

    public void stopLocation() {
        MyLog.d(TAG, "stopLocation");
        if (isLocationUpdating) {
            locationManager.stop();
            lastLocation = null;
        }
        isLocationUpdating = false;
    }

    @Override
    public void onLocationChanged(Location location) {
         MyLog.d(TAG, "onLocationChanged");
        lastLocation.setValue(location);
    }
}