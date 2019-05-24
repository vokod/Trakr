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

    @Inject
    @Named("disc")
    Executor discIoExecutor;
    @Inject
    TrackDao trackDao;
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

    LiveData<List<TrackEntity>> getTracks() {
        return trackDao.loadAll();
    }

    LiveData<List<TrackWithPoints>> getTracksWithPoints() {
        return trackDao.loadAllWithPoints();
    }

    @WorkerThread
    List<TrackEntity> getTracksSync() {
        return trackDao.loadAllSync();
    }


    /**
     * Track methods
     */

    @WorkerThread
    long saveTrackEntitySync(final TrackEntity trackData) {
        return trackDao.save(trackData);
    }


    void saveTrackWithPoints(final TrackWithPoints track) {
        discIoExecutor.execute(() -> {
            TrackEntity entity = TrackWithPoints.fromTrackWithPoints(track);
            long id = trackDao.save(entity);
            List<TrackpointEntity> points = track.getTrackPoints();
            setTrackIdInTrackPointEntities(points,id);
            trackpointDao.saveAll(points);
            trackDao.save(track);
        });
    }

    private void setTrackIdInTrackPointEntities(
            List<TrackpointEntity> trackpointEntities, long id) {
        for (TrackpointEntity trackpointEntity : trackpointEntities) {
            trackpointEntity.setTrackId(id);
            trackpointEntity.setTrackpointId(0);
        }
    }


    void updateTrack(final TrackEntity trackData) {
        discIoExecutor.execute(() -> trackDao.update(trackData));
    }

    LiveData<TrackEntity> getTrack(long id) {
        return trackDao.loadById(id);
    }

    @WorkerThread
    TrackEntity getTrackSync(long id) {
        return trackDao.loadByIdSync(id);
    }

    @WorkerThread
    TrackWithPoints getTrackWithPointsSync(long id) {
        return trackDao.loadByIdWithPointsSync(id);
    }

    @WorkerThread
    void deleteTrack(final long trackId) {
        trackDao.delete(trackId);
    }

    void setTrackFirebaseIdSync(TrackEntity trackEntity, String firebaseId) {
        trackEntity.setFirebaseId(firebaseId);
        trackDao.update(trackEntity);
    }

    /**
     * Trackpoint methods
     */

    void saveTrackpoint(final TrackpointEntity trackpoint) {
        discIoExecutor.execute(() -> {
            // MyLog.d("saveTrackpoint - ", trackpoint.toString());
            trackpointDao.save(trackpoint);
        });
    }

    LiveData<List<TrackpointEntity>> getTrackpointsByTrack(long id) {
        return trackpointDao.loadByTrack(id);
    }

    LiveData<TrackpointEntity> getActualTrackpoint(final long id) {
        return trackpointDao.loadActualTrackpointByTrack(id);
    }
}
