package com.awolity.trakr.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.WorkerThread;

import com.awolity.trakr.TrakrApplication;
import com.awolity.trakr.data.entity.TrackEntity;
import com.awolity.trakr.data.entity.TrackWithPoints;
import com.awolity.trakr.data.entity.TrackpointEntity;
import com.awolity.trakr.gpx.GpxExporter;
import com.awolity.trakr.model.ChartPoint;
import com.awolity.trakr.model.MapPoint;
import com.awolity.trakr.model.TrackData;
import com.awolity.trakr.model.TrackDataTrackEntityConverter;
import com.awolity.trakr.model.TrackDataWithMapPoints;
import com.awolity.trakr.model.TrackPointMapPointConverter;
import com.awolity.trakr.repository.remote.FirestoreTrackRepository;
import com.awolity.trakr.sync.SyncService;
import com.awolity.trakr.utils.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

@Singleton
public class TrackRepository {

    @Inject
    @Named("disc")
    Executor discIoExecutor;

    @Inject
    @Named("transformation")
    Executor transformationExecutor;

    @Inject
    Context context;

    @Inject
    RoomTrackRepository roomTrackRepository;

    @Inject
    FirestoreTrackRepository firestoreTrackRepository;

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

    public LiveData<List<TrackData>> getTracksData() {
        final MediatorLiveData<List<TrackData>> result = new MediatorLiveData<>();
        result.addSource(roomTrackRepository.getTracks(), trackEntities -> {
            if (trackEntities != null) {
                transformationExecutor.execute(() -> {
                    List<TrackData> trackDataList = new ArrayList<>(trackEntities.size());
                    for (TrackEntity entity : trackEntities) {
                        trackDataList.add(TrackDataTrackEntityConverter.toTrackData(entity));
                    }
                    result.postValue(trackDataList);
                });
            }
        });
        return result;
    }

    @WorkerThread
    public List<TrackEntity> getTracksSync() {
        return roomTrackRepository.getTracksSync();
    }

    private void removeFirebaseIdFromTracks() {
        discIoExecutor.execute(() -> {
            List<TrackEntity> tracks = roomTrackRepository.getTracksSync();
            for (TrackEntity entity : tracks) {
                entity.setFirebaseId(null);
                roomTrackRepository.updateTrack(entity);
            }
        });
    }

    /**
     * Track methods
     */

    @WorkerThread
    public long saveTrackSync(final TrackEntity trackEntity) {
        return roomTrackRepository.saveTrackEntitySync(trackEntity);
    }

    public void updateTrackInDb(final TrackEntity trackEntity) {
        roomTrackRepository.updateTrack(trackEntity);
    }


    public void updateTrackTitle(final String title, final long id) {
        discIoExecutor.execute(() -> {
            TrackEntity entity = roomTrackRepository.getTrackSync(id);
            entity.setTitle(title);
            updateTrackInDb(entity);
            if (appUserRepository.IsAppUserLoggedIn()) {
                updateTrackTitleToCloud(entity);
            }
        });
    }

    public LiveData<TrackData> getTrackData(long id) {
        final MediatorLiveData<TrackData> result = new MediatorLiveData<>();
        result.addSource(roomTrackRepository.getTrack(id), trackEntity -> {
            if (trackEntity != null) {
                result.postValue(TrackDataTrackEntityConverter.toTrackData(trackEntity));
            }
        });
        return result;
    }

    public void exportTrack(final long id) {
        discIoExecutor.execute(() -> {
            TrackWithPoints trackWithPoints = roomTrackRepository.getTrackWithPointsSync(id);
            GpxExporter.export(context, trackWithPoints);
        });
    }

    public void deleteTrack(final long trackId) {
        discIoExecutor.execute(() -> {
            TrackEntity entity = roomTrackRepository.getTrackSync(trackId);
            if (entity != null && entity.getFirebaseId() != null) {
                deleteTrackFromCloud(entity.getFirebaseId());
            }
            roomTrackRepository.deleteTrack(trackId);
        });
    }

    public void deleteLocalTrack(final long trackId) {
        roomTrackRepository.deleteTrack(trackId);
    }

    private void deleteSyncedLocalTracks() {
        discIoExecutor.execute(() -> {
            for (TrackEntity trackEntity : roomTrackRepository.getTracksSync()) {
                //noinspection ConstantConditions
                if (trackEntity.getFirebaseId() != null || trackEntity.getFirebaseId().isEmpty()) {
                    deleteLocalTrack(trackEntity.getTrackId());
                }
            }
        });
    }

    /**
     * Trackpoint methods
     */

    public void saveTrackpoint(final TrackpointEntity trackpoint) {
        roomTrackRepository.saveTrackpoint(trackpoint);
    }

   /* public LiveData<List<TrackpointEntity>> getTrackpointsByTrack(long id) {
        return roomTrackRepository.getTrackpointsByTrack(id);
    }*/

    public LiveData<List<ChartPoint>> getChartpointsByTrack(long id, final int chartPointNo) {
        final MediatorLiveData<List<ChartPoint>> result = new MediatorLiveData<>();
        result.addSource(roomTrackRepository.getTrackpointsByTrack(id),
                trackpointEntities -> {
                    if (trackpointEntities != null && trackpointEntities.size() > 0) {
                        transformationExecutor.execute(() -> {
                            long numOfPoints = trackpointEntities.size();
                            List<ChartPoint> chartPoints = new ArrayList<>(chartPointNo);

                            if (numOfPoints > chartPointNo) {
                                long divider = numOfPoints / chartPointNo + 1;

                                double averagedSpeed = 0, averagedAltitude = 0, distance = 0;

                                for (int i = 1; i < numOfPoints; i++) {
                                    // TODO: ez egy kicsit csalás, mert a 0. pont mindenképpen kimarad,
                                    // de az osztás miatt így pontos
                                    // belső ciklusszámlálóval lehetne elegánsabban csinálni.
                                    averagedSpeed += trackpointEntities.get(i).getSpeed();
                                    averagedAltitude += trackpointEntities.get(i).getAltitude();
                                    distance += trackpointEntities.get(i).getDistance();

                                    if (i > 0 && i % divider == 0) {

                                        ChartPoint chartPoint = new ChartPoint();
                                        chartPoint.setTime(
                                                trackpointEntities.get(i).getTime());
                                        chartPoint.setDistance(distance);

                                        chartPoint.setSpeed(averagedSpeed / divider);
                                        chartPoint.setAltitude(averagedAltitude / divider);

                                        chartPoints.add(chartPoint);

                                        averagedSpeed = 0;
                                        averagedAltitude = 0;
                                    }
                                }
                            } else {
                                double distance = 0;
                                for (TrackpointEntity trackpointEntity : trackpointEntities) {
                                    distance += trackpointEntity.getDistance();
                                    chartPoints.add(new ChartPoint(trackpointEntity.getTime(),
                                            trackpointEntity.getAltitude(),
                                            trackpointEntity.getSpeed(),
                                            distance));
                                }
                            }
                            result.postValue(chartPoints);
                        });
                    }
                });
        return result;
    }

    public LiveData<List<MapPoint>> getMapPoints(long id) {
        final MediatorLiveData<List<MapPoint>> mapPoints = new MediatorLiveData<>();
        mapPoints.addSource(roomTrackRepository.getTrackpointsByTrack(id), trackpointEntities -> {
            if (trackpointEntities != null && trackpointEntities.size() > 0) {
                transformationExecutor.execute(() -> {
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
                });
            }
        });
        return mapPoints;
    }

    public LiveData<List<TrackDataWithMapPoints>> getTrackDataListWithMapPoints(
            final int maxNumOfMapPoints) {
        final MediatorLiveData<List<TrackDataWithMapPoints>> result = new MediatorLiveData<>();

        result.addSource(roomTrackRepository.getTracksWithPoints(), trackWithPointsList -> {
            if (trackWithPointsList != null && trackWithPointsList.size() > 0) {
                transformationExecutor.execute(() -> {
                    List<TrackDataWithMapPoints> trackDataWithMapPointsList = new ArrayList<>();
                    for (TrackWithPoints trackWithPoints : trackWithPointsList) {

                        // this is for the event when initial sync is happening right now
                        if (trackWithPoints.getTrackPoints().size() == 0) {
                            continue;
                        }

                        final TrackDataWithMapPoints trackDataWithMapPoints = new TrackDataWithMapPoints();

                        trackDataWithMapPoints.setTrackData(TrackDataTrackEntityConverter
                                .toTrackData(trackWithPoints.getTrackEntity()));

                        long numOfPoints = trackWithPoints.getNumOfTrackPoints();
                        List<MapPoint> mapPoints;
                        if (maxNumOfMapPoints != 0) {
                            mapPoints = new ArrayList<>(maxNumOfMapPoints);
                            if (numOfPoints > maxNumOfMapPoints) {
                                long divider = numOfPoints / maxNumOfMapPoints + 1;

                                for (int i = 0; i < numOfPoints; i += divider) {
                                    mapPoints.add(TrackPointMapPointConverter.toMapPoint(trackWithPoints
                                            .getTrackPoints().get(i)));
                                }
                            } else {
                                for (TrackpointEntity trackPoint : trackWithPoints.getTrackPoints()) {
                                    mapPoints.add(TrackPointMapPointConverter.toMapPoint(trackPoint));
                                }
                            }
                        } else {
                            mapPoints = new ArrayList<>((int) numOfPoints);
                            for (TrackpointEntity trackpointEntity : trackWithPoints.getTrackPoints()) {
                                mapPoints.add(TrackPointMapPointConverter.toMapPoint(trackpointEntity));
                            }
                        }

                        trackDataWithMapPoints.setMapPointList(mapPoints);
                        trackDataWithMapPointsList.add(trackDataWithMapPoints);
                    }
                    result.postValue(trackDataWithMapPointsList);
                });
            }
        });
        return result;
    }

    public LiveData<MapPoint> getActualTrackpoint(final long id) {
        final MediatorLiveData<MapPoint> result = new MediatorLiveData<>();
        result.addSource(roomTrackRepository.getActualTrackpoint(id),
                trackpointEntity -> {
                    if (trackpointEntity != null) {
                        result.setValue(new MapPoint(trackpointEntity.getLatitude(),
                                trackpointEntity.getLongitude()));
                    }
                });
        return result;
    }

    @WorkerThread
    public void saveTrackToCloudOnThread(final long trackId) {
        TrackWithPoints trackWithPoints = roomTrackRepository.getTrackWithPointsSync(trackId);
        TrackEntity trackEntity = TrackEntity.fromTrackWithPoints(trackWithPoints);
        String trackFirebaseId = firestoreTrackRepository.getIdForNewTrack();

        if (trackFirebaseId != null) {
            // update the local instance with the firebase id
            trackWithPoints.setFirebaseId(trackFirebaseId);
            firestoreTrackRepository.saveTrackToCloud(trackWithPoints, trackFirebaseId);
            roomTrackRepository.setTrackFirebaseIdSync(trackEntity, trackFirebaseId);
        }
    }

    public void getAllTrackdatasFromCloud(
            final GetAllTrackDatasFromCloudListener listener) {
        firestoreTrackRepository.getAllTrackDatasFromCloud(listener);
    }

    public void saveTrackToLocalDbFromCloud(final TrackEntity onlineTrack) {
        onlineTrack.setTrackId(0);
        firestoreTrackRepository.getTrackDataFromCloud(onlineTrack.getFirebaseId(), trackEntity ->
                firestoreTrackRepository.getTrackPointsFromCloud(trackEntity,
                        this::saveTrackWithPointsToDb));

    }

    private void saveTrackWithPointsToDb(final TrackWithPoints track) {
        roomTrackRepository.saveTrackWithPoints(track);
    }

    private void updateTrackTitleToCloud(TrackEntity trackEntity) {
        if (trackEntity.getFirebaseId() != null && !trackEntity.getFirebaseId().isEmpty()) {
            firestoreTrackRepository.updateTrackTitleToCloud(trackEntity.getFirebaseId(),
                    trackEntity.getTitle());
        }
    }

    private void deleteTrackFromCloud(String firebaseId) {
        firestoreTrackRepository.deleteTrackFromCloud(firebaseId);
    }

    public interface GetAllTrackDatasFromCloudListener {
        void onAllTrackdatasLoaded(List<TrackEntity> tracks);
    }

    public interface GetTrackDataFromCloudListener {
        void onTrackLoaded(TrackEntity track);
    }

    public interface GetTrackPointsFromCloudListener {
        void onTrackLoaded(TrackWithPoints track);
    }
}
