package com.awolity.trakr.view.detail;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.Nullable;

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

    @SuppressWarnings("WeakerAccess")
    @Inject
    TrackRepository trackRepository;

    @Inject
    SettingsRepository settingsRepository;

    @SuppressWarnings("WeakerAccess")
    @Inject
    @Named("transformation")
    Executor transformationExecutor;

    private long trackId = Constants.NO_LAST_RECORDED_TRACK;

    public TrackDetailViewModel() {
        TrakrApplication.getInstance().getAppComponent().inject(this);
    }

    public void init(long trackId) {
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
            result.addSource(trackRepository.getTrackData(trackId), new Observer<TrackData>() {
                @Override
                public void onChanged(@Nullable TrackData trackData) {
                    if (trackData != null) {
                        trackData.convertToImperial();
                        result.postValue(trackData);
                    }
                }
            });
            return result;
        }
    }

    public LiveData<List<ChartPoint>> getChartPoints() {
        checkTrackId();
        if (getUnit() == Constants.UNIT_METRIC) {
            return trackRepository.getChartpointsByTrack(trackId,
                    Constants.CHART_POINT_MAX_NUMBER_FOR_TRACK_DETAIL);
        } else {
            final MediatorLiveData<List<ChartPoint>> result = new MediatorLiveData<>();
            result.addSource(trackRepository.getChartpointsByTrack(trackId,
                    Constants.CHART_POINT_MAX_NUMBER_FOR_TRACK_DETAIL),
                    new Observer<List<ChartPoint>>() {
                        @Override
                        public void onChanged(@Nullable final List<ChartPoint> chartPoints) {
                            if (chartPoints != null) {
                                transformationExecutor.execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        for (ChartPoint chartPoint : chartPoints) {
                                            chartPoint.convertToImperial();
                                        }
                                        result.postValue(chartPoints);
                                    }
                                });
                            }
                        }
                    });
            return result;
        }
    }

    public LiveData<List<MapPoint>> getMapPoints() {
        checkTrackId();
        return trackRepository.getMapPoints(trackId);
    }

    public void deleteTrack() {
        checkTrackId();
        trackRepository.deleteTrack(trackId);
    }

    public void exportTrack() {
        checkTrackId();
        trackRepository.exportTrack(trackId);
    }

    public void updateTrackTitle(String title) {
        trackRepository.updateTrackTitle(title, trackId);
    }

    private void checkTrackId() {
        if (trackId == -1) {
            throw new IllegalStateException("Viewmodel not initialised");
        }
    }
}
