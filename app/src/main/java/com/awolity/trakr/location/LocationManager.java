package com.awolity.trakr.location;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.awolity.trakr.di.TrakrApplication;
import com.awolity.trakr.utils.MyLog;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import javax.inject.Inject;

public class LocationManager {

    private static final String LOG_TAG = LocationManager.class.getSimpleName();
    private static final int SECOND = 1000;
    private final int locationRequestInterval;
    private final int locationRequestFastestInterval;
    private final int locationRequestPriority;

    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private LocationManagerCallback locationManagerCallback;
    private FusedLocationProviderClient fusedLocationProviderClient;

    @SuppressWarnings("WeakerAccess")
    @Inject
    Context context;
    private boolean isConnected;

    public LocationManager(int locationRequestInterval,
                           int locationRequestFastestInterval, int locationRequestPriority) {
        TrakrApplication.getInstance().getAppComponent().inject(this);

        this.locationRequestInterval = locationRequestInterval * SECOND;
        this.locationRequestFastestInterval = locationRequestFastestInterval * SECOND;
        this.locationRequestPriority = locationRequestPriority;
        createLocationRequest();
    }

    private void createLocationRequest() {
        // MyLog.d(TAG, "createLocationRequest");
        locationRequest = new LocationRequest();
        locationRequest.setInterval(locationRequestInterval);
        locationRequest.setFastestInterval(locationRequestFastestInterval);
        locationRequest.setPriority(locationRequestPriority);
    }

    public void start(LocationManagerCallback locationManagerCallback) {
        // MyLog.d(TAG, "start");
        this.locationManagerCallback = locationManagerCallback;
        setupLocationCallback();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this.context);
        setLocationSettings();
    }

    public void stop() {
        // MyLog.d(TAG, "stop");
        stopLocationUpdates();
        fusedLocationProviderClient = null;
        locationManagerCallback = null;
        locationRequest = null;
        locationCallback = null;
        isConnected = false;
        createLocationRequest();
    }

    public boolean isLocationEnabled() {
        // MyLog.d(TAG, "isLocationEnabled");
        return ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }

    public void isLocationSettingsGood(final LocationSettingsCallback callback) {
        // MyLog.d(TAG, "isLocationSettingsGood");
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        SettingsClient client = LocationServices.getSettingsClient(context);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                // // MyLog.d(LOG_TAG, "isLocationSettingsGood - onSuccess");
                // All location settings are satisfied. The client can initialize
                // location requests here.
                callback.onLocationSettingsDetermined(true);
            }
        });

        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                int statusCode = ((ApiException) e).getStatusCode();
                switch (statusCode) {
                    case CommonStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied, but this can be fixed
                        // by showing the user a dialog.
                        // // MyLog.d(LOG_TAG, "isLocationSettingsGood - onFailure - resolution required");
                        callback.onLocationSettingsDetermined(false);
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way
                        // to fix the settings so we won't show the dialog.
                        // // MyLog.d(LOG_TAG, "isLocationSettingsGood - onFailure - required settings unavailable :(");
                        callback.onLocationSettingsDetermined(false);
                        break;
                }
            }
        });
    }

    private void setupLocationCallback() {
        // MyLog.d(TAG, "setupLocationCallback");
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                // // MyLog.d(LOG_TAG, "new location result");
                // // MyLog.d(LOG_TAG, "    object id: "+ id);
                for (Location location : locationResult.getLocations()) {
                    locationManagerCallback.onLocationChanged(location);
                }
            }
        };
    }

    private void setLocationSettings() {
        // MyLog.d(TAG, "setLocationSettings");
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        SettingsClient client = LocationServices.getSettingsClient(context);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                // // MyLog.d(LOG_TAG, "setLocationSettings - onSuccess");
                // All location settings are satisfied. The client can initialize
                // location requests here.
                isConnected = true;
                startLocationUpdates();
            }
        });
    }

    private void startLocationUpdates() {
        // MyLog.d(TAG, "startLocationUpdates");
        if (isConnected) {
            try {
                fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null /* Looper */);
            } catch (SecurityException e) {
                Log.e(LOG_TAG, "Security Exception. Maybe by not having location permission... " + e.getLocalizedMessage());
            } catch (NullPointerException e) {
                Log.e(LOG_TAG, "Something bad happened:");
                e.printStackTrace();
            }
        }
    }

    private void stopLocationUpdates() {
        // MyLog.d(TAG, "stopLocation");
        try {
            Task<Void> myTask = fusedLocationProviderClient.removeLocationUpdates(locationCallback);
            if (myTask.isSuccessful()) {
                // // MyLog.d(LOG_TAG, "stopLocation - task successful");
            } else {
                // // MyLog.d(LOG_TAG, "stopLocation - task NOT successful");
            }
        } catch (RuntimeException e){
            MyLog.e(LOG_TAG, "unable to remove location callback: "+ e.getLocalizedMessage());
        }
    }

    public int getLocationRequestInterval() {
        return locationRequestInterval;
    }

    public int getLocationRequestFastestInterval() {
        return locationRequestFastestInterval;
    }

    public int getLocationRequestPriority() {
        return locationRequestPriority;
    }

    public interface LocationManagerCallback {
        void onLocationChanged(Location location);
    }

    public interface LocationSettingsCallback {
        void onLocationSettingsDetermined(boolean isSettingsGood);
    }
}