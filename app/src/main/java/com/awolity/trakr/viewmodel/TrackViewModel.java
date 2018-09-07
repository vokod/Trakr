package com.awolity.trakr.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.Nullable;

import com.awolity.trakr.TrakrApplication;
import com.awolity.trakr.data.entity.TrackEntity;
import com.awolity.trakr.data.entity.TrackWithPoints;
import com.awolity.trakr.data.entity.TrackpointEntity;
import com.awolity.trakr.repository.TrackRepository;
import com.awolity.trakr.viewmodel.model.ChartPoint;
import com.awolity.trakr.viewmodel.model.MapPoint;
import com.awolity.trakrutils.Constants;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class TrackViewModel extends ViewModel {

    @SuppressWarnings("WeakerAccess")
    @Inject
    TrackRepository trackRepository;

    private static final String TAG = TrackViewModel.class.getSimpleName();
    private long trackId = Constants.NO_LAST_RECORDED_TRACK;
    private MediatorLiveData<List<MapPoint>> mapPointsMediatorLiveData;
    private MediatorLiveData<List<ChartPoint>> chartPointsMediatorLiveData;

    public TrackViewModel() {
        TrakrApplication.getInstance().getAppComponent().inject(this);
    }

    public void init(long trackId) {
        reset();
        this.trackId = trackId;
    }

    public void reset() {
        trackId = Constants.NO_LAST_RECORDED_TRACK;
        mapPointsMediatorLiveData = new MediatorLiveData<>();
        chartPointsMediatorLiveData = new MediatorLiveData<>();
    }

    public LiveData<TrackEntity> getTrack() {
        checkTrackId();
        return trackRepository.getTrack(trackId);
    }

    public LiveData<TrackWithPoints> getTrackWithPoints() {
        checkTrackId();
        return trackRepository.getTrackWithPoints(trackId);
    }

    public LiveData<TrackEntity> getTrackData() {
        return trackRepository.getTrack(trackId);
    }

    public LiveData<List<ChartPoint>> getChartPoints(final int maxNumOfPoints) {
        checkTrackId();
        chartPointsMediatorLiveData.addSource(trackRepository.getTrackpointsByTrack(trackId),
                new Observer<List<TrackpointEntity>>() {
                    @Override
                    public void onChanged(@Nullable List<TrackpointEntity> trackpointEntities) {
                        if (trackpointEntities != null) {
                            long numOfPoints = trackpointEntities.size();
                            List<ChartPoint> chartPoints = new ArrayList<>(maxNumOfPoints);

                            if (numOfPoints > maxNumOfPoints) {
                                long divider = numOfPoints / maxNumOfPoints + 1;

                                double averagedSpeed = 0, averagedAltitude = 0, distance = 0;

                                for (int i = 1; i < numOfPoints; i++) {
                                    // TODO: ez egy kicsit csalás, mert a 0. pont mindenképpen kimarad, de az osztás miatt így pontos
                                    // belső ciklusszámlálóval lehetne elegánsabban csinálni.
                                    averagedSpeed += trackpointEntities.get(i).getSpeed();
                                    averagedAltitude += trackpointEntities.get(i).getAltitude();
                                    distance += trackpointEntities.get(i).getDistance();

                                    if (i > 0 && i % divider == 0) {

                                        ChartPoint chartPoint = new ChartPoint();
                                        chartPoint.setTime(
                                                trackpointEntities.get(i).getTime());
                                        chartPoint.setDistance(distance);

                                        chartPoint.setSpeed(averagedSpeed / divider);
                                        chartPoint.setAltitude(averagedAltitude / divider);

                                        chartPoints.add(chartPoint);

                                        averagedSpeed = 0;
                                        averagedAltitude = 0;
                                    }
                                }
                            } else {
                                double distance = 0;
                                for (TrackpointEntity trackpointEntity : trackpointEntities) {
                                    distance += trackpointEntity.getDistance();
                                    chartPoints.add(new ChartPoint(trackpointEntity.getTime(),
                                            trackpointEntity.getAltitude(),
                                            trackpointEntity.getSpeed(),
                                            distance));
                                }
                            }
                            chartPointsMediatorLiveData.postValue(chartPoints);
                        }
                    }
                });
        return chartPointsMediatorLiveData;
    }

    public LiveData<List<MapPoint>> getMapPoints() {
        mapPointsMediatorLiveData.addSource(trackRepository.getTrackpointsByTrack(trackId),
                new Observer<List<TrackpointEntity>>() {
                    @Override
                    public void onChanged(@Nullable List<TrackpointEntity> trackpointEntities) {
                        if (trackpointEntities != null) {
                            List<MapPoint> mapPoints = new ArrayList<>(trackpointEntities.size());
                            for (TrackpointEntity trackpointEntity : trackpointEntities) {
                                mapPoints.add(new MapPoint(trackpointEntity.getLatitude(),
                                        trackpointEntity.getLongitude()));
                            }
                            mapPointsMediatorLiveData.postValue(mapPoints);
                        }
                    }
                });
        return mapPointsMediatorLiveData;
    }

    public LiveData<List<TrackpointEntity>> getTrackpointsList() {
        checkTrackId();
        return trackRepository.getTrackpointsByTrack(trackId);
    }

    public LiveData<TrackpointEntity> getActualTrackpoint() {
        checkTrackId();
        return trackRepository.getActualTrackpoint(trackId);
    }

    public void deleteTrack() {
        checkTrackId();
        trackRepository.deleteTrack(trackId);
    }

    public void exportTrack() {
        checkTrackId();
        trackRepository.exportTrack(trackId);
    }

    public void finishRecording() {
        reset();
    }

    public void updateTrack(TrackEntity trackEntity) {
        trackRepository.updateTrack(trackEntity);
    }

    private void checkTrackId() {
        if (trackId == -1) {
            throw new IllegalStateException("Viewmodel not initialised");
        }
    }


}
