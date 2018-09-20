package com.awolity.trakr.view.main;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.location.Location;

import com.awolity.trakr.TrakrApplication;
import com.awolity.trakr.data.entity.TrackEntity;
import com.awolity.trakr.data.entity.TrackWithPoints;
import com.awolity.trakr.data.entity.TrackpointEntity;
import com.awolity.trakr.location.LocationManager;
import com.awolity.trakr.repository.SettingsRepository;
import com.awolity.trakr.repository.TrackRepository;
import com.awolity.trakr.model.ChartPoint;
import com.awolity.trakr.model.MapPoint;
import com.awolity.trakrutils.Constants;
import com.google.android.gms.location.LocationRequest;

import java.util.List;

import javax.inject.Inject;

public class MainActivityViewModel extends AndroidViewModel implements LocationManager.LocationManagerCallback  {

    private final LocationManager locationManager;
    private MutableLiveData<Location> lastLocation;
    private boolean isLocationUpdating;
    private long trackId = Constants.NO_LAST_RECORDED_TRACK;

    @SuppressWarnings("WeakerAccess")
    @Inject
    TrackRepository trackRepository;

    @SuppressWarnings("WeakerAccess")
    @Inject
    SettingsRepository settingsRepository;

    public MainActivityViewModel(Application application) {
        super(application);
        TrakrApplication.getInstance().getAppComponent().inject(this);
        locationManager = new LocationManager(2, 1,
                LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    public void init(long trackId) {
        this.trackId = trackId;
    }

    public void reset() {
        trackId = Constants.NO_LAST_RECORDED_TRACK;
    }

    public void isLocationSettingsGood(LocationManager.LocationSettingsCallback callback) {
        locationManager.isLocationSettingsGood(callback);
    }

    public LiveData<Location> getLocation() {
        isLocationUpdating = true;
        if (lastLocation == null) {
            lastLocation = new MutableLiveData<>();
            locationManager.start(this);
        }
        return lastLocation;
    }

    public void stopLocation() {
        if (isLocationUpdating) {
            locationManager.stop();
            lastLocation = null;
        }
        isLocationUpdating = false;
    }

    @Override
    public void onLocationChanged(Location location) {
        // MyLog.d(TAG, "onLocationChanged");
        lastLocation.setValue(location);
    }

    public LiveData<TrackEntity> getTrack() {
        checkTrackId();
        return trackRepository.getTrack(trackId);
    }

    public LiveData<TrackWithPoints> getTrackWithPoints() {
        checkTrackId();
        return trackRepository.getTrackWithPoints(trackId);
    }

    public LiveData<TrackEntity> getTrackData() {
        return trackRepository.getTrack(trackId);
    }

    public LiveData<List<TrackpointEntity>> getTrackpointsList() {
        checkTrackId();
        return trackRepository.getTrackpointsByTrack(trackId);
    }

    public LiveData<TrackpointEntity> getActualTrackpoint() {
        checkTrackId();
        return trackRepository.getActualTrackpoint(trackId);
    }

    public void finishRecording() {
        reset();
    }

    private void checkTrackId() {
        if (trackId == -1) {
            throw new IllegalStateException("Viewmodel not initialised");
        }
    }

    public long getLastRecordedTrackId(){
        return settingsRepository.getLastRecordedTrackId();
    }

    public void setLastRecordedTrackId(long trackId){
        settingsRepository.setLastRecordedTrackId(trackId);
    }
}
