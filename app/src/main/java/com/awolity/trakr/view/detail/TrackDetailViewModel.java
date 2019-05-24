package com.awolity.trakr.view.detail;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;

import com.awolity.trakr.TrakrApplication;
import com.awolity.trakr.model.ChartPoint;
import com.awolity.trakr.model.MapPoint;
import com.awolity.trakr.model.TrackData;
import com.awolity.trakr.repository.SettingsRepository;
import com.awolity.trakr.repository.TrackRepository;
import com.awolity.trakr.utils.Constants;

import java.util.List;
import java.util.concurrent.Executor;

import javax.inject.Inject;
import javax.inject.Named;

public class TrackDetailViewModel extends ViewModel {

    @Inject
    TrackRepository trackRepository;

    @Inject
    SettingsRepository settingsRepository;

    @Inject
    @Named("transformation")
    Executor transformationExecutor;

    private long trackId = Constants.NO_LAST_RECORDED_TRACK;

    public TrackDetailViewModel() {
        TrakrApplication.getInstance().getAppComponent().inject(this);
    }

    void init(long trackId) {
        this.trackId = trackId;
    }

    public int getUnit() {
        return settingsRepository.getUnit();
    }

    public LiveData<TrackData> getTrackData() {
        checkTrackId();
        if (getUnit() == Constants.UNIT_METRIC) {
            return trackRepository.getTrackData(trackId);
        } else {
            final MediatorLiveData<TrackData> result = new MediatorLiveData<>();
            result.addSource(trackRepository.getTrackData(trackId), trackData -> {
                if (trackData != null) {
                    trackData.convertToImperial();
                    result.postValue(trackData);
                }
            });
            return result;
        }
    }

    LiveData<List<ChartPoint>> getChartPoints() {
        checkTrackId();
        if (getUnit() == Constants.UNIT_METRIC) {
            return trackRepository.getChartpointsByTrack(trackId,
                    Constants.CHART_POINT_MAX_NUMBER_FOR_TRACK_DETAIL);
        } else {
            final MediatorLiveData<List<ChartPoint>> result = new MediatorLiveData<>();
            result.addSource(trackRepository.getChartpointsByTrack(trackId,
                    Constants.CHART_POINT_MAX_NUMBER_FOR_TRACK_DETAIL),
                    chartPoints -> {
                        if (chartPoints != null) {
                            transformationExecutor.execute(() -> {
                                for (ChartPoint chartPoint : chartPoints) {
                                    chartPoint.convertToImperial();
                                }
                                result.postValue(chartPoints);
                            });
                        }
                    });
            return result;
        }
    }

    LiveData<List<MapPoint>> getMapPoints() {
        checkTrackId();
        return trackRepository.getMapPoints(trackId);
    }

    void deleteTrack() {
        checkTrackId();
        trackRepository.deleteTrack(trackId);
    }

    void exportTrack() {
        checkTrackId();
        trackRepository.exportTrack(trackId);
    }

    void updateTrackTitle(String title) {
        trackRepository.updateTrackTitle(title, trackId);
    }

    private void checkTrackId() {
        if (trackId == -1) {
            throw new IllegalStateException("Viewmodel not initialised");
        }
    }
}
