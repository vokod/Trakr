package com.awolity.trakr.sync;

import com.awolity.trakr.TrakrApplication;
import com.awolity.trakr.repository.local.model.entity.TrackEntity;
import com.awolity.trakr.repository.TrackRepository;

import java.util.List;
import java.util.concurrent.Executor;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

@Singleton
public class DbSanitizer {

    @Inject
    TrackRepository trackRepository;

    @Inject
    @Named("disc")
    Executor discIoExecutor;

    DbSanitizer() {
        TrakrApplication.getInstance().getAppComponent().inject(this);
    }

    void sanitizeDb() {
        discIoExecutor.execute(() -> {
            List<TrackEntity> trackEntities = trackRepository.getTracksSync();
            for (TrackEntity trackEntity : trackEntities) {
                if (trackEntity.getNumOfTrackPoints() < 2) {
                    trackRepository.deleteTrack(trackEntity.getTrackId());
                }
            }
        });
    }
}
