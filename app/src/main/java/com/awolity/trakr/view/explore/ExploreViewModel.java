package com.awolity.trakr.view.explore;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;

import com.awolity.trakr.TrakrApplication;
import com.awolity.trakr.view.model.MapPoint;
import com.awolity.trakr.view.model.TrackData;
import com.awolity.trakr.repository.SettingsRepository;
import com.awolity.trakr.repository.TrackRepository;
import com.awolity.trakr.utils.Constants;

import java.util.List;

import javax.inject.Inject;

public class ExploreViewModel extends ViewModel {

    @Inject
    TrackRepository trackRepository;

    @Inject
    SettingsRepository settingsRepository;

    public ExploreViewModel() {
        TrakrApplication.getInstance().getAppComponent().inject(this);
    }

    public int getUnit() {
        return settingsRepository.getUnit();
    }

    LiveData<List<TrackData>> getTracksData() {
        if (getUnit() == Constants.UNIT_METRIC) {
            return trackRepository.getTracksData();
        } else {
            final MediatorLiveData<List<TrackData>> result = new MediatorLiveData<>();
            result.addSource(trackRepository.getTracksData(), trackDataList -> {
                if (trackDataList != null) {
                    for (TrackData trackData : trackDataList) {
                        trackData.convertToImperial();
                    }
                    result.postValue(trackDataList);
                }
            });
            return result;
        }
    }

    LiveData<List<MapPoint>> getMapPointsOfTrack(long id) {
        return trackRepository.getMapPoints(id);
    }
}
