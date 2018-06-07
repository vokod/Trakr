package com.awolity.trakr.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.awolity.trakr.data.entity.TrackEntity;
import com.awolity.trakr.data.entity.TrackWithPoints;
import com.awolity.trakr.data.entity.TrackpointEntity;
import com.awolity.trakr.di.TrakrApplication;
import com.awolity.trakr.repository.Repository;
import com.awolity.trakr.utils.MyLog;

import java.util.List;

import javax.inject.Inject;

public class TrackViewModel extends ViewModel {

    @SuppressWarnings("WeakerAccess")
    @Inject
    Repository repository;

    public static final long NOT_SET = -1;
    private static final String LOG_TAG = TrackViewModel.class.getSimpleName();
    private long trackId = NOT_SET;

    public TrackViewModel() {

        TrakrApplication.getInstance().getAppComponent().inject(this);
    }

    public void init(long trackId, Class caller) {
        MyLog.d(LOG_TAG, "init- trackId: " + trackId + " caller: " + caller.getSimpleName());
        if (this.trackId != NOT_SET) {
            throw new IllegalStateException("Viewmodel already initialised");
        }
        this.trackId = trackId;
    }

    public void reset(){
        trackId =NOT_SET;
    }

    public LiveData<TrackEntity> getTrack() {
        checkTrackId();
        return repository.getTrack(trackId);
    }

    public LiveData<TrackWithPoints> getTrackWithPoints() {
        checkTrackId();
        return repository.getTrackWithPoints(trackId);
    }

    public LiveData<List<TrackpointEntity>> getTrackpointsList() {
        checkTrackId();
        return repository.getTrackpointsByTrack(trackId);
    }

    public LiveData<TrackpointEntity> getActualTrackpoint() {
        checkTrackId();
        return repository.getActualTrackpoint(trackId);
    }

    public void deleteTrack() {
        checkTrackId();
        repository.deleteTrack(trackId);
    }

    public void exportTrack() {
        checkTrackId();
        repository.exportTrack(trackId);
    }

    public void updateTrack(TrackEntity trackEntity) {
        repository.updateTrack(trackEntity);
    }

    private void checkTrackId() {
        if (trackId == -1) {
            throw new IllegalStateException("Viewmodel not initialised");
        }
    }
}
