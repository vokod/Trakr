package com.awolity.trakr.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.Nullable;

import com.awolity.trakr.data.entity.TrackEntity;
import com.awolity.trakr.data.entity.TrackWithPoints;
import com.awolity.trakr.di.TrakrApplication;
import com.awolity.trakr.repository.Repository;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class TrackListViewModel extends ViewModel {

    @SuppressWarnings("WeakerAccess")
    @Inject
    Repository repository;

    private LiveData<List<TrackEntity>> tracks;
    private LiveData<List<TrackWithPoints>> tracksWithPoints;

    private static final String LOG_TAG = TrackListViewModel.class.getSimpleName();

    public TrackListViewModel() {
        TrakrApplication.getInstance().getAppComponent().inject(this);
    }

    public LiveData<List<TrackEntity>> getTracks() {
        if (tracks == null) {
            tracks = repository.getTracks();
        }
        return tracks;
    }

    public LiveData<List<TrackWithPoints>> getTracksWithPoints(){
        if (tracksWithPoints == null) {
            tracksWithPoints = repository.getTracksWithPoints();
        }
        return tracksWithPoints;
    }
}
