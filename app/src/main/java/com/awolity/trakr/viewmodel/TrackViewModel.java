package com.awolity.trakr.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.awolity.trakr.data.entity.TrackEntity;
import com.awolity.trakr.data.entity.TrackWithPoints;
import com.awolity.trakr.data.entity.TrackpointEntity;
import com.awolity.trakr.di.TrakrApplication;
import com.awolity.trakr.repository.Repository;

import java.util.List;

import javax.inject.Inject;

public class TrackViewModel extends ViewModel {

    @SuppressWarnings("WeakerAccess")
    @Inject
    Repository repository;

    private static final String LOG_TAG = TrackViewModel.class.getSimpleName();
    private long trackId;

    public TrackViewModel() {
        // MyLog.d(LOG_TAG, "TrackViewModel");
        TrakrApplication.getInstance().getAppComponent().inject(this);
    }

    public void init(long trackId) {
        this.trackId = trackId;
    }

    public LiveData<TrackEntity> getTrack() {
        return repository.getTrack(trackId);
    }

    public LiveData<TrackWithPoints> getTrackWithPoints(){
        return repository.getTrackWithPoints(trackId);
    }

    public LiveData<List<TrackpointEntity>> getTrackpointsList() {
        return repository.getTrackpointsByTrack(trackId);
    }

    public LiveData<TrackpointEntity> getActualTrackpoint() {
        return repository.getActualTrackpoint(trackId);
    }

    public void deleteTrack() {
        repository.deleteTrack(trackId);
    }

    public void exportTrack(){
        repository.exportTrack(trackId);
    }
}
