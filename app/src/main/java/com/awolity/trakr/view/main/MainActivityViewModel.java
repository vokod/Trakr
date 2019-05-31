package com.awolity.trakr.view.main;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import android.location.Location;

import com.awolity.trakr.TrakrApplication;
import com.awolity.trakr.location.LocationManager;
import com.awolity.trakr.view.model.ChartPoint;
import com.awolity.trakr.view.model.MapPoint;
import com.awolity.trakr.view.model.TrackData;
import com.awolity.trakr.repository.SettingsRepository;
import com.awolity.trakr.repository.TrackRepository;
import com.awolity.trakr.utils.Constants;
import com.google.android.gms.location.LocationRequest;

import java.util.List;

import javax.inject.Inject;

public class MainActivityViewModel extends AndroidViewModel implements LocationManager.LocationManagerCallback {

    private final LocationManager locationManager;
    private MutableLiveData<Location> lastLocation;
    private boolean isLocationUpdating;
    private long trackId = Constants.NO_LAST_RECORDED_TRACK;

    @Inject
    TrackRepository trackRepository;

    @Inject
    SettingsRepository settingsRepository;

    public MainActivityViewModel(Application application) {
        super(application);
        TrakrApplication.getInstance().getAppComponent().inject(this);
        locationManager = new LocationManager(2, 1,
                LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    void init(long trackId) {
        this.trackId = trackId;
    }

    void reset() {
        trackId = Constants.NO_LAST_RECORDED_TRACK;
    }

    void isLocationSettingsGood(LocationManager.LocationSettingsCallback callback) {
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
        lastLocation.setValue(location);
    }

    public LiveData<TrackData> getTrackData() {
        checkTrackId();
        return trackRepository.getTrackData(trackId);
    }

     LiveData<List<MapPoint>> getMapPoints() {
        checkTrackId();
        return trackRepository.getMapPoints(trackId);
    }

     LiveData<MapPoint> getActualMapPoint() {
        checkTrackId();
        return trackRepository.getActualTrackpoint(trackId);
    }

    public LiveData<List<ChartPoint>> getChartPoints(){
        checkTrackId();
        return trackRepository.getChartpointsByTrack(trackId,
                Constants.CHART_POINT_MAX_NUMBER_FOR_BOTTOM_SHEET_CHARTS_FRAGMENT);
    }

    void finishRecording() {
        reset();
    }

    private void checkTrackId() {
        if (trackId == -1) {
            throw new IllegalStateException("Viewmodel not initialised");
        }
    }

    long getLastRecordedTrackId() {
        return settingsRepository.getLastRecordedTrackId();
    }

    void clearLastRecordedTrackId() {
        settingsRepository.setLastRecordedTrackId(Constants.NO_LAST_RECORDED_TRACK);
    }
}
