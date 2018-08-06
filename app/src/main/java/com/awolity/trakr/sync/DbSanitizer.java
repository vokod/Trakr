package com.awolity.trakr.sync;

import com.awolity.trakr.data.entity.TrackEntity;
import com.awolity.trakr.TrakrApplication;
import com.awolity.trakr.repository.TrackRepository;
import com.awolity.trakr.utils.MyLog;

import java.util.List;
import java.util.concurrent.Executor;

import javax.inject.Inject;

public class DbSanitizer {

    private static final String TAG = DbSanitizer.class.getSimpleName();

    @Inject
    TrackRepository trackRepository;

    @Inject
    Executor discIoExecutor;

    public DbSanitizer() {
      // MyLog.d(TAG, "DbSanitizer");
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
