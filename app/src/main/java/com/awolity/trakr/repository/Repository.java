package com.awolity.trakr.repository;

import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.support.annotation.WorkerThread;

import com.awolity.trakr.data.dao.TrackDao;
import com.awolity.trakr.data.dao.TrackpointDao;
import com.awolity.trakr.data.entity.TrackEntity;
import com.awolity.trakr.data.entity.TrackWithPoints;
import com.awolity.trakr.data.entity.TrackpointEntity;
import com.awolity.trakr.di.TrakrApplication;
import com.awolity.trakr.gpx.GpxExporter;

import java.util.List;
import java.util.concurrent.Executor;

import javax.inject.Inject;

public class Repository {

    private static final String LOG_TAG = Repository.class.getSimpleName();

    @Inject
    Executor discIoExecutor;
    @Inject
    TrackDao trackDao;
    @Inject
    TrackpointDao trackpointDao;
    @Inject
    Context context;
    private long id;

    public Repository() {
        TrakrApplication.getInstance().getAppComponent().inject(this);
    }

    /**
     * TrackS methods
     */

    public LiveData<List<TrackEntity>> getTracks() {
        return trackDao.loadAll();
    }

    public LiveData<List<TrackWithPoints>> getTracksWithPoints() {
        return trackDao.loadAllWithPoints();
    }

    /**
     * Track methods
     */

    public long saveTrack(final TrackEntity trackData) {
        // MyLog.d(LOG_TAG, "saveTrack");
        discIoExecutor.execute(new Runnable() {
            @Override
            public void run() {
                id = trackDao.save(trackData);
            }
        });
        return id;
    }

    @WorkerThread
    public long saveTrackSync(final TrackEntity trackData) {
        // MyLog.d(LOG_TAG, "saveTrackSync - id:" + trackData.getTrackId());
        return trackDao.save(trackData);
    }

    public void updateTrack(final TrackEntity trackData) {
        // MyLog.d(LOG_TAG, "updateTrack - id:" + trackData.getTrackId());
        discIoExecutor.execute(new Runnable() {
            @Override
            public void run() {
                trackDao.update(trackData);
            }
        });
    }

    public LiveData<TrackEntity> getTrack(long id) {
        // MyLog.d(LOG_TAG, "getTrack - id:" + id);
        return trackDao.loadById(id);
    }

    public LiveData<TrackWithPoints> getTrackWithPoints(long id) {
        // MyLog.d(LOG_TAG, "getTrack - id:" + id);
        return trackDao.loadByIdWithPoints(id);
    }

    public void exportTrack(final long id) {
        discIoExecutor.execute(new Runnable() {
            @Override
            public void run() {
                TrackWithPoints trackWithPoints = trackDao.loadByIdWithPointsSync(id);
                GpxExporter.export(context, trackWithPoints);
            }
        });
    }

    public void deleteTrack(final long trackId) {
        discIoExecutor.execute(new Runnable() {
            @Override
            public void run() {
                trackDao.delete(trackId);
            }
        });
    }

    /**
     * Trackpoint methods
     */

    public void saveTrackpoint(final TrackpointEntity trackpoint) {
        // MyLog.d(LOG_TAG, "saveTrackpoint");
        discIoExecutor.execute(new Runnable() {
            @Override
            public void run() {
                trackpointDao.save(trackpoint);
            }
        });
    }

    public LiveData<List<TrackpointEntity>> getTrackpointsByTrack(long id) {
        // MyLog.d(LOG_TAG, "getTrackpointsByTrack");
        return trackpointDao.loadByTrack(id);
    }

    public LiveData<TrackpointEntity> getActualTrackpoint(final long id) {
        return trackpointDao.loadActualTrackpointByTrack(id);
    }
}
