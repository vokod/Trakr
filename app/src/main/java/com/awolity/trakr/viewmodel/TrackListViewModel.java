package com.awolity.trakr.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.Nullable;

import com.awolity.trakr.TrakrApplication;
import com.awolity.trakr.data.entity.TrackWithPoints;
import com.awolity.trakr.data.entity.TrackpointEntity;
import com.awolity.trakr.repository.SettingsRepository;
import com.awolity.trakr.repository.TrackRepository;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class TrackListViewModel extends ViewModel {

    @SuppressWarnings("WeakerAccess")
    @Inject
    TrackRepository trackRepository;

    @Inject
    SettingsRepository settingsRepository;

    private LiveData<List<TrackWithPoints>> tracksWithPoints;
    private final MediatorLiveData<List<TrackWithPoints>> simplifiedTracksWithPoints;

    private static final String TAG = TrackListViewModel.class.getSimpleName();

    public TrackListViewModel() {
        TrakrApplication.getInstance().getAppComponent().inject(this);
        simplifiedTracksWithPoints = new MediatorLiveData<>();
    }

    public LiveData<List<TrackWithPoints>> getTracksWithPoints() {
        if (tracksWithPoints == null) {
            tracksWithPoints = trackRepository.getTracksWithPoints();
        }
        return tracksWithPoints;
    }

    public LiveData<List<TrackWithPoints>> getSimplifiedTracksWithPoints(final int maxNumOfPoints) {
        simplifiedTracksWithPoints.addSource(trackRepository.getTracksWithPoints(),
                new Observer<List<TrackWithPoints>>() {
                    @Override
                    public void onChanged(@Nullable List<TrackWithPoints> tracksWithPoints) {
                        if (tracksWithPoints != null) {
                            List<TrackWithPoints> result = new ArrayList<>(tracksWithPoints.size());
                            long trackBeingRecordedRightNowId = settingsRepository.getLastRecordedTrackId();
                            for (TrackWithPoints originalTrackWithPoints : tracksWithPoints) {

                                if (originalTrackWithPoints.getTrackId() == trackBeingRecordedRightNowId) {
                                    continue;
                                }
                                // this is for the event when initial sync is happening right now
                                if (originalTrackWithPoints.getTrackPoints().size() == 0) {
                                    continue;
                                }

                                long numOfPoints = originalTrackWithPoints.getNumOfTrackPoints();
                                if (numOfPoints > maxNumOfPoints) {
                                    long divider = numOfPoints / maxNumOfPoints + 1;

                                    TrackWithPoints oneResult = new TrackWithPoints();
                                    oneResult.setTrackEntity(
                                            originalTrackWithPoints.getTrackEntity());

                                    List<TrackpointEntity> simplifiedList = new ArrayList<>(maxNumOfPoints);
                                    for (int i = 0; i < numOfPoints; i += divider) {
                                        simplifiedList.add(
                                                originalTrackWithPoints.getTrackPoints().get(i));
                                    }
                                    oneResult.setTrackPoints(simplifiedList);
                                    result.add(oneResult);
                                } else {
                                    result.add(originalTrackWithPoints);
                                }
                            }
                            simplifiedTracksWithPoints.postValue(result);
                        }
                    }
                });
        return simplifiedTracksWithPoints;
    }
}
