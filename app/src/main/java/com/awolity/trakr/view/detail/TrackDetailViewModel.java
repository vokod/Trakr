package com.awolity.trakr.view.detail;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.awolity.trakr.TrakrApplication;
import com.awolity.trakr.model.ChartPoint;
import com.awolity.trakr.model.MapPoint;
import com.awolity.trakr.model.TrackData;
import com.awolity.trakr.repository.TrackRepository;
import com.awolity.trakrutils.Constants;

import java.util.List;

import javax.inject.Inject;

public class TrackDetailViewModel extends ViewModel {

    @SuppressWarnings("WeakerAccess")
    @Inject
    TrackRepository trackRepository;

    private long trackId = Constants.NO_LAST_RECORDED_TRACK;

    public TrackDetailViewModel() {
        TrakrApplication.getInstance().getAppComponent().inject(this);
    }

    public void init(long trackId) {
        this.trackId = trackId;
    }

    public LiveData<TrackData> getTrackData() {
        checkTrackId();
        return trackRepository.getTrackData(trackId);
    }

    public LiveData<List<ChartPoint>> getChartPoints(int pointMaxNo) {
        checkTrackId();
        return trackRepository.getChartpointsByTrack(trackId,  pointMaxNo);
    }

    public LiveData<List<MapPoint>> getMapPoints() {
        checkTrackId();
        return trackRepository.getMapPoints(trackId);
    }

    public void deleteTrack() {
        checkTrackId();
        trackRepository.deleteTrack(trackId);
    }

    public void exportTrack() {
        checkTrackId();
        trackRepository.exportTrack(trackId);
    }

    public void updateTrackTitle(String title) {
        trackRepository.updateTrackTitle(title, trackId);
    }

    private void checkTrackId() {
        if (trackId == -1) {
            throw new IllegalStateException("Viewmodel not initialised");
        }
    }


}
