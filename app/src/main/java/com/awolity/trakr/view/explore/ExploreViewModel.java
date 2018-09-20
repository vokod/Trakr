package com.awolity.trakr.view.explore;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.awolity.trakr.TrakrApplication;
import com.awolity.trakr.model.MapPoint;
import com.awolity.trakr.model.TrackData;
import com.awolity.trakr.repository.SettingsRepository;
import com.awolity.trakr.repository.TrackRepository;

import java.util.List;

import javax.inject.Inject;

public class ExploreViewModel extends ViewModel {

    @SuppressWarnings("WeakerAccess")
    @Inject
    TrackRepository trackRepository;

    @Inject
    SettingsRepository settingsRepository;

    private LiveData<List<TrackData>> tracksData;

    public ExploreViewModel() {
        TrakrApplication.getInstance().getAppComponent().inject(this);

    }

    public LiveData<List<TrackData>> getTracksData() {
        if (tracksData == null) {
            tracksData = trackRepository.getTracksData();
        }
        return tracksData;
    }

    public LiveData<List<MapPoint>> getMapPointsOfTrack(long id){
        return trackRepository.getMapPoints(id);
    }


}
