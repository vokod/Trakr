package com.awolity.trakr.view.explore;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;
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
        final MediatorLiveData<List<TrackData>> result = new MediatorLiveData<>();
        result.addSource(trackRepository.getTracksData(), new Observer<List<TrackData>>() {
            @Override
            public void onChanged(List<TrackData> trackDatas) {
                long lastRecordedTrackId = settingsRepository.getLastRecordedTrackId();
                if (lastRecordedTrackId != Constants.NO_LAST_RECORDED_TRACK) {
                    for (TrackData trackData : trackDatas) {
                        if (trackData.getTrackId() == lastRecordedTrackId) {
                            trackDatas.remove(trackData);
                        }
                    }
                }
                if (getUnit() == Constants.UNIT_IMPERIAL) {
                    for (TrackData trackData : trackDatas) {
                        trackData.convertToImperial();
                    }
                }
                result.postValue(trackDatas);
            }
        });
        return result;
    }

    LiveData<List<MapPoint>> getMapPointsOfTrack(long id) {
        return trackRepository.getMapPoints(id);
    }
}
