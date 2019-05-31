package com.awolity.trakr.view.list;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;

import com.awolity.trakr.TrakrApplication;
import com.awolity.trakr.view.model.TrackDataWithMapPoints;
import com.awolity.trakr.repository.SettingsRepository;
import com.awolity.trakr.repository.TrackRepository;
import com.awolity.trakr.utils.Constants;

import java.util.List;

import javax.inject.Inject;

public class TrackListViewModel extends ViewModel {

    @Inject
    TrackRepository trackRepository;

    @Inject
    SettingsRepository settingsRepository;

    public TrackListViewModel() {
        TrakrApplication.getInstance().getAppComponent().inject(this);
    }

    public int getUnit() {
        return settingsRepository.getUnit();
    }

    LiveData<List<TrackDataWithMapPoints>> getTrackDataListWithMapPoints() {
        final MediatorLiveData<List<TrackDataWithMapPoints>> result = new MediatorLiveData<>();
        result.addSource(trackRepository.getTrackDataListWithMapPoints(
                Constants.MAP_POINT_MAX_NUMBER_FOR_TRACK_LIST),
                trackDataListWithMapPoints -> {
                    if (trackDataListWithMapPoints != null && trackDataListWithMapPoints.size() != 0) {
                        // get out the last recorded track, if recording right now
                        long lastRecordedTrackId = settingsRepository.getLastRecordedTrackId();
                        if (lastRecordedTrackId != Constants.NO_LAST_RECORDED_TRACK) {
                            for (TrackDataWithMapPoints trackDataWithMapPoints : trackDataListWithMapPoints) {
                                if (trackDataWithMapPoints.getTrackData().getTrackId() == lastRecordedTrackId) {
                                    trackDataListWithMapPoints.remove(trackDataWithMapPoints);
                                    break;
                                }
                            }
                        }
                        if (settingsRepository.getUnit() == Constants.UNIT_IMPERIAL) {
                            for (TrackDataWithMapPoints trackDataWithMapPoints : trackDataListWithMapPoints) {
                                trackDataWithMapPoints.getTrackData().convertToImperial();
                                //noinspection ResultOfMethodCallIgnored
                                trackDataWithMapPoints.getTrackData().getTrackId();
                            }
                        }
                        result.postValue(trackDataListWithMapPoints);
                    }
                });
        return result;

    }
}
