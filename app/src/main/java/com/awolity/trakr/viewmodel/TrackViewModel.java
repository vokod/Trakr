package com.awolity.trakr.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.awolity.trakr.data.entity.TrackEntity;
import com.awolity.trakr.data.entity.TrackWithPoints;
import com.awolity.trakr.data.entity.TrackpointEntity;
import com.awolity.trakr.TrakrApplication;
import com.awolity.trakr.repository.TrackRepository;
import com.awolity.trakr.utils.Constants;
import com.awolity.trakr.utils.MyLog;
import com.awolity.trakr.view.detail.TrackDetailActivity;
import com.awolity.trakr.view.main.MainActivity;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class TrackViewModel extends ViewModel {

    @SuppressWarnings("WeakerAccess")
    @Inject
    TrackRepository trackRepository;

    private static final long NOT_SET = -1;
    private static final String TAG = TrackViewModel.class.getSimpleName();
    private long trackId = NOT_SET;
    private MediatorLiveData<TrackWithPoints> simplifiedTrackWithPoints;

    public TrackViewModel() {
        TrakrApplication.getInstance().getAppComponent().inject(this);
    }

    public void init(long trackId) {
        if (this.trackId == NOT_SET) {
            this.trackId = trackId;
        }
        simplifiedTrackWithPoints = new MediatorLiveData<>();
    }

    public void reset() {
        trackId = NOT_SET;
    }

    public LiveData<TrackEntity> getTrack() {
        checkTrackId();
        return trackRepository.getTrack(trackId);
    }

    public LiveData<TrackWithPoints> getTrackWithPoints() {
        checkTrackId();
        return trackRepository.getTrackWithPoints(trackId);
    }

    public LiveData<TrackWithPoints> getSimplifiedTrackWithPoints(final int maxNumOfPoints) {
        checkTrackId();
        simplifiedTrackWithPoints.addSource(trackRepository.getTrackWithPoints(trackId),
                new Observer<TrackWithPoints>() {
                    @Override
                    public void onChanged(@Nullable TrackWithPoints trackWithPoints) {
                        if (trackWithPoints != null) {
                            long numOfPoints = trackWithPoints.getNumOfTrackPoints();

                            if (numOfPoints > maxNumOfPoints) {
                                long divider = numOfPoints / maxNumOfPoints + 1;
                                TrackWithPoints result = new TrackWithPoints();
                                result.setTrackEntity(
                                        trackWithPoints.getTrackEntity());

                                List<TrackpointEntity> simplifiedList = new ArrayList<>();
                                double averagedSpeed = 0, averagedAltitude = 0;

                                // TODO: igazából nem is entytiben kellene ezt visszaadni a view-nak
                                // annak úgysem kell tudni az entity-kről, hanem valami egyszerűbb adat
                                // szerkezetben
                                // ez ugyanígy igaz a többi viewmodelre is
                                for (int i = 0; i < numOfPoints; i++) {
                                    if (i > 0 && i % divider == 0) {

                                        TrackpointEntity averagedTrackpoint = new TrackpointEntity();
                                        averagedTrackpoint.setTime(
                                                trackWithPoints.getTrackPoints().get(i).getTime());
                                        averagedTrackpoint.setDistance(
                                                trackWithPoints.getTrackPoints().get(i).getDistance());
                                        averagedTrackpoint.setLatitude(
                                                trackWithPoints.getTrackPoints().get(i).getLatitude());
                                        averagedTrackpoint.setLongitude(
                                                trackWithPoints.getTrackPoints().get(i).getLongitude());

                                        averagedTrackpoint.setSpeed(averagedSpeed / divider);
                                        averagedTrackpoint.setAltitude(averagedAltitude / divider);

                                        simplifiedList.add(averagedTrackpoint);

                                        averagedSpeed = 0;
                                        averagedAltitude = 0;
                                    } else {
                                        averagedSpeed = averagedSpeed
                                                + trackWithPoints.getTrackPoints().get(i).getSpeed();
                                        averagedAltitude = averagedAltitude
                                                + trackWithPoints.getTrackPoints().get(i).getAltitude();
                                    }
                                }
                                result.setTrackPoints(simplifiedList);
                                simplifiedTrackWithPoints.postValue(result);
                            } else {
                                simplifiedTrackWithPoints.postValue(trackWithPoints);
                            }
                        }
                    }
                });
        return simplifiedTrackWithPoints;
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
        trackRepository.saveTrackToCloud(trackId);
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
