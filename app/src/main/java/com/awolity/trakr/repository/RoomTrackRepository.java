package com.awolity.trakr.repository;

import androidx.lifecycle.LiveData;
import android.content.Context;
import androidx.annotation.WorkerThread;

import com.awolity.trakr.TrakrApplication;
import com.awolity.trakr.data.dao.TrackDao;
import com.awolity.trakr.data.dao.TrackpointDao;
import com.awolity.trakr.data.entity.TrackEntity;
import com.awolity.trakr.data.entity.TrackWithPoints;
import com.awolity.trakr.data.entity.TrackpointEntity;

import java.util.List;
import java.util.concurrent.Executor;

import javax.inject.Inject;
import javax.inject.Named;

public class RoomTrackRepository {

    @SuppressWarnings("WeakerAccess")
    @Inject
    @Named("disc")
    Executor discIoExecutor;
    @SuppressWarnings("WeakerAccess")
    @Inject
    TrackDao trackDao;
    @SuppressWarnings("WeakerAccess")
    @Inject
    TrackpointDao trackpointDao;
    @Inject
    Context context;

    public RoomTrackRepository() {
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

    @WorkerThread
    public List<TrackEntity> getTracksSync() {
        return trackDao.loadAllSync();
    }


    /**
     * Track methods
     */

    @WorkerThread
    public long saveTrackEntitySync(final TrackEntity trackData) {
        return trackDao.save(trackData);
    }


    public long saveTrackWithPoints(final TrackWithPoints track) {
        discIoExecutor.execute(() -> {
            TrackEntity entity = TrackWithPoints.fromTrackWithPoints(track);
            long id = trackDao.save(entity);
            List<TrackpointEntity> points = track.getTrackPoints();
            setTrackIdInTrackPointEntities(points,id);
            trackpointDao.saveAll(points);
        });
        return trackDao.save(track);
    }

    private void setTrackIdInTrackPointEntities(
            List<TrackpointEntity> trackpointEntities, long id) {
        for (TrackpointEntity trackpointEntity : trackpointEntities) {
            trackpointEntity.setTrackId(id);
            trackpointEntity.setTrackpointId(0);
        }
    }


    public void updateTrack(final TrackEntity trackData) {
        discIoExecutor.execute(new Runnable() {
            @Override
            public void run() {
                trackDao.update(trackData);
            }
        });
    }

    public LiveData<TrackEntity> getTrack(long id) {
        return trackDao.loadById(id);
    }

    @WorkerThread
    public TrackEntity getTrackSync(long id) {
        return trackDao.loadByIdSync(id);
    }

    public LiveData<TrackWithPoints> getTrackWithPoints(long id) {
        return trackDao.loadByIdWithPoints(id);
    }

    @WorkerThread
    public TrackWithPoints getTrackWithPointsSync(long id) {
        return trackDao.loadByIdWithPointsSync(id);
    }

    @WorkerThread
    public void deleteTrack(final long trackId) {
        trackDao.delete(trackId);
    }

    public void setTrackFirebaseIdSync(TrackEntity trackEntity, String firebaseId) {
        trackEntity.setFirebaseId(firebaseId);
        trackDao.update(trackEntity);
    }

    /**
     * Trackpoint methods
     */

    public void saveTrackpoint(final TrackpointEntity trackpoint) {
        discIoExecutor.execute(new Runnable() {
            @Override
            public void run() {
                // MyLog.d("saveTrackpoint - ", trackpoint.toString());
                trackpointDao.save(trackpoint);
            }
        });
    }

    public LiveData<List<TrackpointEntity>> getTrackpointsByTrack(long id) {
        return trackpointDao.loadByTrack(id);
    }

    public LiveData<TrackpointEntity> getActualTrackpoint(final long id) {
        return trackpointDao.loadActualTrackpointByTrack(id);
    }

    public void saveAllTrackpoints(List<TrackpointEntity> trackpointEntities) {
        trackpointDao.saveAll(trackpointEntities);
    }
}
