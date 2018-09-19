package com.awolity.trakr.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;

import com.awolity.trakr.TrakrApplication;
import com.awolity.trakr.data.entity.TrackEntity;
import com.awolity.trakr.data.entity.TrackWithPoints;
import com.awolity.trakr.data.entity.TrackpointEntity;
import com.awolity.trakr.gpx.GpxExporter;
import com.awolity.trakr.model.MapPoint;
import com.awolity.trakr.model.TrackData;
import com.awolity.trakr.model.TrackDataTrackEntityConverter;
import com.awolity.trakr.model.TrackPointMapPointConverter;
import com.awolity.trakr.sync.SyncService;
import com.awolity.trakrutils.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class TrackRepository {

    @SuppressWarnings("WeakerAccess")
    @Inject
    Executor discIoExecutor;

    @SuppressWarnings("WeakerAccess")
    @Inject
    Context context;

    @SuppressWarnings("WeakerAccess")
    @Inject
    RoomTrackRepository roomTrackRepository;

    @SuppressWarnings("WeakerAccess")
    @Inject
    FirebaseTrackRepository firebaseTrackRepository;

    @SuppressWarnings("WeakerAccess")
    @Inject
    AppUserRepository appUserRepository;

    public TrackRepository() {
        TrakrApplication.getInstance().getAppComponent().inject(this);
        appUserRepository.setAppUserStatusListener(new AppUserRepository.AppUserStatusListener() {
            @Override
            public void onSignOut() {
                // delete all tracks locally, that have a Firebase Id
                deleteSyncedLocalTracks();
            }

            @Override
            public void onSignIn() {
                // sync tracks with cloud
                context.getApplicationContext().startService(
                        new Intent(context.getApplicationContext(), SyncService.class));
            }

            @Override
            public void onDeleteAccount() {
                removeFirebaseIdFromTracks();
            }
        });
    }

    /**
     * TrackS methods
     */

    public LiveData<List<TrackWithPoints>> getTracksWithPoints() {
        return roomTrackRepository.getTracksWithPoints();
    }

    public LiveData<List<TrackData>> getTracksData() {
        final MediatorLiveData<List<TrackData>> result = new MediatorLiveData<>();
        result.addSource(roomTrackRepository.getTracks(), new Observer<List<TrackEntity>>() {
            @Override
            public void onChanged(@Nullable List<TrackEntity> trackEntities) {
                if (trackEntities != null) {
                    List<TrackData> trackDataList = new ArrayList<>(trackEntities.size());
                    for (TrackEntity entity : trackEntities) {
                        trackDataList.add(TrackDataTrackEntityConverter.toTrackData(entity));
                    }
                    result.postValue(trackDataList);
                }
            }
        });
        return result;
    }

    @WorkerThread
    public List<TrackEntity> getTracksSync() {
        return roomTrackRepository.getTracksSync();
    }

    @SuppressWarnings("WeakerAccess")
    private void removeFirebaseIdFromTracks() {
        discIoExecutor.execute(new Runnable() {
            @Override
            public void run() {
                List<TrackEntity> tracks = roomTrackRepository.getTracksSync();
                for (TrackEntity entity : tracks) {
                    entity.setFirebaseId(null);
                    roomTrackRepository.updateTrack(entity);
                }
            }
        });
    }

    /**
     * Track methods
     */

    @WorkerThread
    public long saveTrackSync(final TrackEntity trackEntity) {
        return roomTrackRepository.saveTrackSync(trackEntity);
    }

    public void saveTrackToCloud(final long trackId) {
        discIoExecutor.execute(new Runnable() {
            @Override
            public void run() {
                if (roomTrackRepository.getTrackSync(trackId).getNumOfTrackPoints() > 1) {
                    saveTrackToCloudOnThread(trackId);
                }
            }
        });
    }

    public void updateTrack(final TrackEntity trackEntity) {
        roomTrackRepository.updateTrack(trackEntity);
        if (appUserRepository.IsAppUserLoggedIn()) {
            updateTrackToCloud(trackEntity);
        }
    }

    public void updateTrackTitle(final String title, final long id) {
        discIoExecutor.execute(new Runnable() {
            @Override
            public void run() {
                TrackEntity entity = roomTrackRepository.getTrackSync(id);
                entity.setTitle(title);
                updateTrack(entity);
                if (appUserRepository.IsAppUserLoggedIn()) {
                    updateTrackToCloud(entity);
                }
            }
        });
    }

    public LiveData<TrackEntity> getTrack(long id) {
        return roomTrackRepository.getTrack(id);
    }

    public LiveData<TrackData> getTrackData(long id) {
        final MediatorLiveData<TrackData> result = new MediatorLiveData<>();
        result.addSource(roomTrackRepository.getTrack(id), new Observer<TrackEntity>() {
            @Override
            public void onChanged(@Nullable TrackEntity trackEntity) {
                if (trackEntity != null) {
                    result.postValue(TrackDataTrackEntityConverter.toTrackData(trackEntity));
                }
            }
        });
        return result;
    }


    public LiveData<TrackWithPoints> getTrackWithPoints(long id) {
        return roomTrackRepository.getTrackWithPoints(id);
    }

    public LiveData<List<MapPoint>> getMapPoints(long id) {
        final MediatorLiveData<List<MapPoint>> mapPoints = new MediatorLiveData<>();
        mapPoints.addSource(roomTrackRepository.getTrackpointsByTrack(id), new Observer<List<TrackpointEntity>>() {
            @Override
            public void onChanged(@Nullable List<TrackpointEntity> trackpointEntities) {
                if (trackpointEntities != null && trackpointEntities.size() > 0) {
                    List<MapPoint> result = new ArrayList<>(Constants.MAP_POINT_MAX_NUMBER_FOR_EXPLORE);
                    long numOfPoints = trackpointEntities.size();
                    if (numOfPoints > Constants.MAP_POINT_MAX_NUMBER_FOR_EXPLORE) {
                        long divider = numOfPoints / Constants.MAP_POINT_MAX_NUMBER_FOR_EXPLORE + 1;

                        for (int i = 0; i < numOfPoints; i += divider) {
                            result.add(TrackPointMapPointConverter.toMapPoint(trackpointEntities.get(i)));
                        }
                    } else {
                        for (TrackpointEntity trackpointEntity : trackpointEntities) {
                            result.add(TrackPointMapPointConverter.toMapPoint(trackpointEntity));
                        }
                    }
                    mapPoints.postValue(result);
                }
            }
        });
        return mapPoints;
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

    private void deleteSyncedLocalTracks() {
        discIoExecutor.execute(new Runnable() {
            @Override
            public void run() {
                for (TrackEntity trackEntity : roomTrackRepository.getTracksSync()) {
                    if (trackEntity.getFirebaseId() != null || trackEntity.getFirebaseId().isEmpty()) {
                        deleteLocalTrack(trackEntity.getTrackId());
                    }
                }
            }
        });
    }

    public void deleteLocalTrack(final long trackId) {
        roomTrackRepository.deleteTrack(trackId);
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

    @WorkerThread
    public void saveTrackToCloudOnThread(final long trackId) {
        TrackWithPoints trackWithPoints = roomTrackRepository.getTrackWithPointsSync(trackId);
        TrackEntity trackEntity = TrackEntity.fromTrackWithPoints(trackWithPoints);
        String trackFirebaseId = firebaseTrackRepository.getIdForNewTrack();
        // TODO: ezt átalakítani úgy, hogy a user logint hamarabb teszteljük
        if (trackFirebaseId != null) {
            // update the local instance with the firebase id
            roomTrackRepository.setTrackFirebaseIdSync(trackEntity, trackFirebaseId);
            trackWithPoints.setFirebaseId(trackFirebaseId);
            firebaseTrackRepository.saveTrackToCloudOnThread(trackWithPoints, trackFirebaseId);
        }
    }

    private void updateTrackToCloud(TrackEntity trackEntity) {
        if (trackEntity.getFirebaseId() != null && !trackEntity.getFirebaseId().isEmpty()) {
            firebaseTrackRepository.updateTrackToCloud(trackEntity);
        }
    }

    public void getAllTrackEntitiesFromCloud(final GetAllTrackEntitiesFromCloudListener listener) {
        firebaseTrackRepository.getAllTrackEntitiesFromCloud(listener);
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
            trackpointEntity.setTrackpointId(0);
        }
    }

    private void deleteTrackFromCloud(String firebaseId) {
        firebaseTrackRepository.deleteTrackFromCloud(firebaseId);
    }

    public interface GetAllTrackEntitiesFromCloudListener {
        void onAllTracksLoaded(List<TrackEntity> trackEntityList);
    }

    public interface GetTrackpointsFromCloudListener {
        void onTrackpointsLoaded(List<TrackpointEntity> trackpointEntityList);
    }

}
