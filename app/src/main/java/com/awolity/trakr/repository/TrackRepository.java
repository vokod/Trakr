package com.awolity.trakr.repository;

import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.support.annotation.WorkerThread;

import com.awolity.trakr.data.entity.TrackEntity;
import com.awolity.trakr.data.entity.TrackWithPoints;
import com.awolity.trakr.data.entity.TrackpointEntity;
import com.awolity.trakr.TrakrApplication;
import com.awolity.trakr.gpx.GpxExporter;
import com.awolity.trakr.utils.PreferenceUtils;

import java.util.List;
import java.util.concurrent.Executor;

import javax.inject.Inject;

public class TrackRepository {

    @Inject
    Executor discIoExecutor;

    @Inject
    Context context;

    @Inject
    RoomTrackRepository roomTrackRepository;

    @Inject
    FirebaseTrackRepository firebaseTrackRepository;

    public TrackRepository() {
        TrakrApplication.getInstance().getAppComponent().inject(this);
    }

    /**
     * TrackS methods
     */

    public LiveData<List<TrackEntity>> getTracks() {
        return roomTrackRepository.getTracks();
    }

    public LiveData<List<TrackWithPoints>> getTracksWithPoints() {
        return roomTrackRepository.getTracksWithPoints();
    }

    /**
     * Track methods
     */

    @WorkerThread
    public long saveTrackSync(final TrackEntity trackData) {
        return roomTrackRepository.saveTrackSync(trackData);
    }

    public void updateTrack(final TrackEntity trackData) {
        roomTrackRepository.updateTrack(trackData);
        // TODO
        if (trackData.getFirebaseId() != null) {
            // if it was already in firebase than update it
            updateTrackToCloud(trackData);
        }
    }

    public LiveData<TrackEntity> getTrack(long id) {
        return roomTrackRepository.getTrack(id);
    }

    @WorkerThread
    public List<TrackEntity> getTracksSync() {
        return roomTrackRepository.getTracksSync();
    }

    public LiveData<TrackWithPoints> getTrackWithPoints(long id) {
        return roomTrackRepository.getTrackWithPoints(id);
    }

    public void exportTrack(final long id) {
        discIoExecutor.execute(new Runnable() {
            @Override
            public void run() {
                TrackWithPoints trackWithPoints = roomTrackRepository.getTrackWithPointsSync(id);
                GpxExporter.export(context, trackWithPoints);
            }
        });
    }

    public void deleteTrack(final long trackId) {
        discIoExecutor.execute(new Runnable() {
            @Override
            public void run() {
                TrackEntity entity = roomTrackRepository.getTrackSync(trackId);
                if (entity != null && entity.getFirebaseId() != null) {
                    deleteTrackFromCloud(entity.getFirebaseId());
                }
                roomTrackRepository.deleteTrack(trackId);
            }
        });
    }

    /**
     * Trackpoint methods
     */

    public void saveTrackpoint(final TrackpointEntity trackpoint) {
        roomTrackRepository.saveTrackpoint(trackpoint);
    }

    public LiveData<List<TrackpointEntity>> getTrackpointsByTrack(long id) {
        return roomTrackRepository.getTrackpointsByTrack(id);
    }

    public LiveData<TrackpointEntity> getActualTrackpoint(final long id) {
        return roomTrackRepository.getActualTrackpoint(id);
    }

    /**
     * Firebase
     */

    public void saveTrackToCloud(final long trackId) {
        discIoExecutor.execute(new Runnable() {
            @Override
            public void run() {
                saveTrackToCloudOnThread(trackId);
            }
        });
    }

    @WorkerThread
    public void saveTrackToCloudOnThread(final long trackId) {
        TrackWithPoints trackWithPoints = roomTrackRepository.getTrackWithPointsSync(trackId);
        TrackEntity trackEntity = TrackEntity.fromTrackWithPoints(trackWithPoints);
        String trackFirebaseId = firebaseTrackRepository.getIdForNewTrack();
        roomTrackRepository.setTrackFirebaseIdSync(trackEntity,trackFirebaseId);
        firebaseTrackRepository.saveTrackToCloudOnThread(trackWithPoints, trackFirebaseId);
    }

    private void updateTrackToCloud(TrackEntity trackEntity) {
       firebaseTrackRepository.updateTrackToCloud(trackEntity);
    }

    public void getAllTrackEntitiesFromCloud(final GetAllTrackEntitiesFromCloudListener listener) {
        firebaseTrackRepository.getAllTrackEntitiesFromCloud(listener);
    }

    public interface GetAllTrackEntitiesFromCloudListener {
        void onAllTracksLoaded(List<TrackEntity> trackEntityList);
    }

    public interface GetTrackpointsFromCloudListener{
        void onTrackpointsLoaded(List<TrackpointEntity> trackpointEntityList);
    }

    public void saveTrackToLocalDbFromCloud(final TrackEntity onlineTrackEntity) {
        onlineTrackEntity.setTrackId(0);

        firebaseTrackRepository.getTrackPoints(onlineTrackEntity.getFirebaseId(),
                new GetTrackpointsFromCloudListener() {
                    @Override
                    public void onTrackpointsLoaded(List<TrackpointEntity> trackpointEntityList) {
                        saveTrackEntityWithPointsToDb(onlineTrackEntity, trackpointEntityList);
                    }
                });
    }

    @WorkerThread
    private void saveTrackEntityWithPointsToDb(final TrackEntity trackEntity,
                                               final List<TrackpointEntity> trackpointEntities) {
        discIoExecutor.execute(new Runnable() {
            @Override
            public void run() {
                final long trackId = roomTrackRepository.saveTrackSync(trackEntity);
                setTrackIdInTrackPointEntities(trackpointEntities, trackId);
                roomTrackRepository.saveAllTrackpoints(trackpointEntities);
            }
        });
    }

    private void setTrackIdInTrackPointEntities(
            List<TrackpointEntity> trackpointEntities, long id) {
        for (TrackpointEntity trackpointEntity : trackpointEntities) {
            trackpointEntity.setTrackId(id);
        }
    }

    private void deleteTrackFromCloud(String firebaseId) {
      firebaseTrackRepository.deleteTrackFromCloud(firebaseId);
    }

    public void setInstallationId(){
        firebaseTrackRepository.setInstallationId();
    }
}
