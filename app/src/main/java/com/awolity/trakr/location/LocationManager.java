package com.awolity.trakr.location;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import android.util.Log;

import com.awolity.trakr.TrakrApplication;
import com.crashlytics.android.Crashlytics;
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

    private static final String TAG = LocationManager.class.getSimpleName();
    private static final int SECOND = 1000;
    private final int locationRequestInterval;
    private final int locationRequestFastestInterval;
    private final int locationRequestPriority;

    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private LocationManagerCallback locationManagerCallback;
    private FusedLocationProviderClient fusedLocationProviderClient;

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

        task.addOnSuccessListener(locationSettingsResponse -> {
            // MyLog.d(TAG, "isLocationSettingsGood - onSuccess");
            // All location settings are satisfied. The client can initialize
            // location requests here.
            callback.onLocationSettingsDetermined(true, null);
        });

        task.addOnFailureListener(e -> {
            int statusCode = ((ApiException) e).getStatusCode();
            switch (statusCode) {
                case CommonStatusCodes.RESOLUTION_REQUIRED:
                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                    // Location settings are not satisfied. However, we have no way
                    // to fix the settings so we won't show the dialog.
                    // MyLog.d(TAG, "isLocationSettingsGood - onFailure - required settings unavailable :(");
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.
                    // MyLog.d(TAG, "isLocationSettingsGood - onFailure - resolution required");
                    callback.onLocationSettingsDetermined(false, e);
                    break;
            }
        });
    }

    private void setupLocationCallback() {
        // MyLog.d(TAG, "setupLocationCallback");
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                // MyLog.d(TAG, "new location result");
                // MyLog.d(TAG, "    object id: "+ id);
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

        task.addOnSuccessListener(locationSettingsResponse -> {
            // MyLog.d(TAG, "setLocationSettings - onSuccess");
            // All location settings are satisfied. The client can initialize
            // location requests here.
            isConnected = true;
            startLocationUpdates();
        });
    }

    @SuppressLint("LogNotTimber")
    private void startLocationUpdates() {
        // MyLog.d(TAG, "startLocationUpdates");
        if (isConnected) {
            try {
                fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null /* Looper */);
            } catch (SecurityException e) {
                Crashlytics.logException(e);
                Log.e(TAG, "Security Exception. Maybe by not having location permission... " + e.getLocalizedMessage());
            } catch (NullPointerException e) {
                Crashlytics.logException(e);
                Log.e(TAG, "Something bad happened:");
                e.printStackTrace();
            }
        }
    }

    private void stopLocationUpdates() {
        // MyLog.d(TAG, "stopLocation");
        try {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback);
        } catch (RuntimeException e) {
            Crashlytics.logException(e);
            // MyLog.e(TAG, "unable to remove location callback: " + e.getLocalizedMessage());
        }
    }

    public int getLocationRequestInterval() {
        return locationRequestInterval;
    }

    public int getLocationRequestPriority() {
        return locationRequestPriority;
    }

    public interface LocationManagerCallback {
        void onLocationChanged(Location location);
    }

    public interface LocationSettingsCallback {
        void onLocationSettingsDetermined(boolean isSettingsGood, Exception e);
    }
}