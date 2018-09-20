package com.awolity.trakr.view.explore;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.Nullable;

import com.awolity.trakr.TrakrApplication;
import com.awolity.trakr.model.MapPoint;
import com.awolity.trakr.model.TrackData;
import com.awolity.trakr.repository.SettingsRepository;
import com.awolity.trakr.repository.TrackRepository;
import com.awolity.trakrutils.Constants;

import java.util.List;

import javax.inject.Inject;

public class ExploreViewModel extends ViewModel {

    @SuppressWarnings("WeakerAccess")
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

    public LiveData<List<TrackData>> getTracksData() {
        if (getUnit() == Constants.UNIT_METRIC) {
            return trackRepository.getTracksData();
        } else {
            final MediatorLiveData<List<TrackData>> result = new MediatorLiveData<>();
            result.addSource(trackRepository.getTracksData(), new Observer<List<TrackData>>() {
                @Override
                public void onChanged(@Nullable List<TrackData> trackDataList) {
                    if (trackDataList != null) {
                        for(TrackData trackData : trackDataList){
                            trackData.convertToImperial();
                        }
                        result.postValue(trackDataList);
                    }
                }
            });
            return result;
        }
    }

    public LiveData<List<MapPoint>> getMapPointsOfTrack(long id) {
        return trackRepository.getMapPoints(id);
    }
}
