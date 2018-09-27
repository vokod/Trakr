package com.awolity.trakr.sync;

import com.awolity.trakr.TrakrApplication;
import com.awolity.trakr.data.entity.TrackEntity;
import com.awolity.trakr.repository.TrackRepository;

import java.util.List;
import java.util.concurrent.Executor;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

@Singleton
public class DbSanitizer {

    @SuppressWarnings("WeakerAccess")
    @Inject
    TrackRepository trackRepository;

    @SuppressWarnings("WeakerAccess")
    @Inject
    @Named("disc")
    Executor discIoExecutor;

    public DbSanitizer() {
        TrakrApplication.getInstance().getAppComponent().inject(this);
    }

    public void sanitizeDb() {
        discIoExecutor.execute(new Runnable() {
            @Override
            public void run() {
                List<TrackEntity> trackEntities = trackRepository.getTracksSync();
                for (TrackEntity trackEntity : trackEntities) {
                    if (trackEntity.getNumOfTrackPoints() < 2) {
                        trackRepository.deleteTrack(trackEntity.getTrackId());
                    }
                }
            }
        });
    }
}
