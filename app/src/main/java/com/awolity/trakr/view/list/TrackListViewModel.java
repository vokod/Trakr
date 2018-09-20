package com.awolity.trakr.view.list;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.Nullable;

import com.awolity.trakr.TrakrApplication;
import com.awolity.trakr.model.TrackData;
import com.awolity.trakr.model.TrackDataWithMapPoints;
import com.awolity.trakr.repository.SettingsRepository;
import com.awolity.trakr.repository.TrackRepository;
import com.awolity.trakrutils.Constants;

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

    public int getUnit(){
        return settingsRepository.getUnit();
    }

    public LiveData<List<TrackDataWithMapPoints>> getTrackDataListWithMapPoints() {
        final MediatorLiveData<List<TrackDataWithMapPoints>> result = new MediatorLiveData<>();
        result.addSource(trackRepository.getTrackDataListWithMapPoints(
                Constants.MAP_POINT_MAX_NUMBER_FOR_TRACK_LIST),
                new Observer<List<TrackDataWithMapPoints>>() {
                    @Override
                    public void onChanged(@Nullable List<TrackDataWithMapPoints> trackDataListWithMapPoints) {
                        if (trackDataListWithMapPoints != null) {
                            // get out the last recorded track, if recording right now
                            long lastRecordedTrackId = settingsRepository.getLastRecordedTrackId();
                            for (TrackDataWithMapPoints trackDataWithMapPoints : trackDataListWithMapPoints) {
                                if (trackDataWithMapPoints.getTrackData().getTrackId() == lastRecordedTrackId) {
                                    trackDataListWithMapPoints.remove(trackDataWithMapPoints);
                                    break;
                                }
                            }
                            if (settingsRepository.getUnit() == Constants.VALUE_UNIT_IMPERIAL) {
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
