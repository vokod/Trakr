package com.awolity.trakr.repository;

import android.arch.lifecycle.LiveData;
import android.support.annotation.WorkerThread;

import com.awolity.trakr.data.dao.TrackDao;
import com.awolity.trakr.data.dao.TrackpointDao;
import com.awolity.trakr.data.entity.TrackEntity;
import com.awolity.trakr.data.entity.TrackpointEntity;
import com.awolity.trakr.di.TrakrApplication;

import java.util.List;
import java.util.concurrent.Executor;

import javax.inject.Inject;

public class Repository {

    private static final String TAG = Repository.class.getSimpleName();

    @Inject
    Executor discIoExecutor;

    @Inject
    TrackDao trackDao;

    @Inject
    TrackpointDao trackpointDao;

    public Repository() {
        TrakrApplication.getInstance().getAppComponent().inject(this);
    }

    private long id;

    public long saveTrack(final TrackEntity trackData) {
        // MyLog.d(TAG, "saveTrack");
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
        // MyLog.d(TAG, "saveTrackSync - id:" + trackData.getTrackId());
        return trackDao.save(trackData);
    }

    public void updateTrack(final TrackEntity trackData) {
        // MyLog.d(TAG, "updateTrack - id:" + trackData.getTrackId());
        discIoExecutor.execute(new Runnable() {
            @Override
            public void run() {
                trackDao.update(trackData);
            }
        });
    }

    public LiveData<TrackEntity> getTrack(long id) {
        // MyLog.d(TAG, "getTrack - id:" + id);
        return trackDao.loadById(id);
    }

    public TrackEntity getTrackSync(long id) {
        // MyLog.d(TAG, "getTrackSync - id:" + id);
        return trackDao.loadByIdSync(id);
    }

    public LiveData<Integer> getTrackNumOfTrackpoints(long id) {
        // MyLog.d(TAG, "getTrackNumOfTrackpoints - id:" + id);
        return trackDao.loadNumOftrackpointsById(id);
    }

    public void saveTrackpoint(final TrackpointEntity trackpoint) {
        // MyLog.d(TAG, "saveTrackpoint");
        discIoExecutor.execute(new Runnable() {
            @Override
            public void run() {
                trackpointDao.save(trackpoint);
            }
        });
    }

    public LiveData<List<TrackpointEntity>> getTrackpointsByTrack(long id) {
        // MyLog.d(TAG, "getTrackpointsByTrack");
        return trackpointDao.loadByTrack(id);
    }

    public LiveData<TrackpointEntity> getActualTrackpoint(final long id) {
        return trackpointDao.loadActualTrackpointByTrack(id);
    }

    public LiveData<List<TrackEntity>> getTracks() {
        return trackDao.loadAll();
    }
}
