package com.awolity.trakr.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.awolity.trakr.data.entity.TrackEntity;
import com.awolity.trakr.data.entity.TrackWithPoints;
import com.awolity.trakr.TrakrApplication;
import com.awolity.trakr.repository.TrackRepository;

import java.util.List;

import javax.inject.Inject;

public class TrackListViewModel extends ViewModel {

    @SuppressWarnings("WeakerAccess")
    @Inject
    TrackRepository trackRepository;

    private LiveData<List<TrackEntity>> tracks;
    private LiveData<List<TrackWithPoints>> tracksWithPoints;

    private static final String TAG = TrackListViewModel.class.getSimpleName();

    public TrackListViewModel() {
        TrakrApplication.getInstance().getAppComponent().inject(this);
    }

    public LiveData<List<TrackEntity>> getTracks() {
        if (tracks == null) {
            tracks = trackRepository.getTracks();
        }
        return tracks;
    }

    public LiveData<List<TrackWithPoints>> getTracksWithPoints(){
        if (tracksWithPoints == null) {
            tracksWithPoints = trackRepository.getTracksWithPoints();
        }
        return tracksWithPoints;
    }
}
