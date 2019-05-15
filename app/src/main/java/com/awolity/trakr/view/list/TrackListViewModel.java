package com.awolity.trakr.view.list;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.annotation.Nullable;

import com.awolity.trakr.TrakrApplication;
import com.awolity.trakr.model.TrackDataWithMapPoints;
import com.awolity.trakr.repository.SettingsRepository;
import com.awolity.trakr.repository.TrackRepository;
import com.awolity.trakr.utils.Constants;

import java.util.List;

import javax.inject.Inject;

public class TrackListViewModel extends ViewModel {

    @SuppressWarnings("WeakerAccess")
    @Inject
    TrackRepository trackRepository;

    @SuppressWarnings("WeakerAccess")
    @Inject
    SettingsRepository settingsRepository;

    public TrackListViewModel() {
        TrakrApplication.getInstance().getAppComponent().inject(this);
    }

    public int getUnit() {
        return settingsRepository.getUnit();
    }

    public LiveData<List<TrackDataWithMapPoints>> getTrackDataListWithMapPoints() {
        final MediatorLiveData<List<TrackDataWithMapPoints>> result = new MediatorLiveData<>();
        result.addSource(trackRepository.getTrackDataListWithMapPoints(
                Constants.MAP_POINT_MAX_NUMBER_FOR_TRACK_LIST),
                new Observer<List<TrackDataWithMapPoints>>() {
                    @Override
                    public void onChanged(@Nullable List<TrackDataWithMapPoints> trackDataListWithMapPoints) {
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
                                    trackDataWithMapPoints.getTrackData().getTrackId();
                                }
                            }
                            result.postValue(trackDataListWithMapPoints);
                        }
                    }
                });
        return result;

    }
}
