package com.awolity.trakr.repository;

import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;

import com.awolity.trakr.data.dao.TrackDao;
import com.awolity.trakr.data.dao.TrackpointDao;
import com.awolity.trakr.data.dao.TrackpointDao_Impl;
import com.awolity.trakr.data.entity.TrackEntity;
import com.awolity.trakr.data.entity.TrackWithPoints;
import com.awolity.trakr.data.entity.TrackpointEntity;
import com.awolity.trakr.di.TrakrApplication;
import com.awolity.trakr.gpx.GpxExporter;
import com.awolity.trakr.utils.MyLog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

import javax.inject.Inject;

public class TrackRepository {

    private static final String LOG_TAG = TrackRepository.class.getSimpleName();

    @Inject
    Executor discIoExecutor;
    @Inject
    TrackDao trackDao;
    @Inject
    TrackpointDao trackpointDao;
    @Inject
    Context context;

    public TrackRepository() {
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
        if (trackData.getFirebaseId() != null) {
            // if it was already in firebase than update it
            updateTrackToCloud(trackData);
        }
    }

    public LiveData<TrackEntity> getTrack(long id) {
        // MyLog.d(LOG_TAG, "getTrack - id:" + id);
        return trackDao.loadById(id);
    }

    @WorkerThread
    public List<TrackEntity> getTracksSync() {
        MyLog.d(LOG_TAG, "getTracksSync");
        return trackDao.loadAllSync();
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
                TrackEntity entity = trackDao.loadByIdSync(trackId);
                if (entity != null && entity.getFirebaseId() != null) {
                    deleteTrackFromCloud(entity.getFirebaseId());
                }
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

    /**
     * Firebase
     */


    public void saveTrackToCloud(final long trackId) {
        MyLog.d(LOG_TAG, "saveTrackToCloud");
        discIoExecutor.execute(new Runnable() {
            @Override
            public void run() {
               saveTrackToCloudOnThread(trackId);
            }
        });
    }

    @WorkerThread
    public void saveTrackToCloudOnThread(final long trackId){
        final String appUserId = FirebaseAuth.getInstance().getUid();
        if (appUserId == null) {
            return;
        }

        TrackWithPoints trackWithPoints = trackDao.loadByIdWithPointsSync(trackId);
        TrackEntity entity = TrackEntity.fromTrackWithPoints(trackWithPoints);

        final DatabaseReference dbReference
                = FirebaseDatabase.getInstance().getReference();
        String trackFirebaseId = dbReference
                .child("tracks")
                .child(appUserId).push().getKey();

        entity.setFirebaseId(trackFirebaseId);
        trackDao.update(entity);

        Map<String, Object> childUpdates = new HashMap<>();
        // create chat in "chatsLiveData" node
        childUpdates.put("tracks"
                + "/"
                + appUserId
                + "/"
                + trackFirebaseId, entity);
        childUpdates.put("trackpoints"
                + "/"
                + appUserId
                + "/"
                + trackFirebaseId, trackWithPoints.getTrackPoints());
        dbReference.updateChildren(childUpdates);
    }

    private void updateTrackToCloud(TrackEntity trackEntity) {
        MyLog.d(LOG_TAG, "updateTrackToCloud");
        final String appUserId = FirebaseAuth.getInstance().getUid();
        if (appUserId == null) {
            return;
        }

        final DatabaseReference trackDbReference
                = FirebaseDatabase.getInstance().getReference()
                .child("tracks")
                .child(appUserId).child(trackEntity.getFirebaseId());
        trackDbReference.setValue(trackEntity);
    }

    public void getAllTrackEntitiesFromCloud(final GetAllTrackEntitiesFromFirebaseListener listener) {
        MyLog.d(LOG_TAG, "getAllTrackEntitiesFromCloud");
        final String appUserId = FirebaseAuth.getInstance().getUid();
        if (appUserId == null) {
            return;
        }

        final DatabaseReference tracksDbReference
                = FirebaseDatabase.getInstance().getReference()
                .child("tracks")
                .child(appUserId);

        tracksDbReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<TrackEntity> trackEntities = new ArrayList<>();
                for (DataSnapshot trackEntitySnapshot : dataSnapshot.getChildren()) {
                    trackEntities.add(trackEntitySnapshot.getValue(TrackEntity.class));
                }
                listener.onAllTracksLoaded(trackEntities);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                MyLog.e(LOG_TAG, "Error in getAllTrackEntitiesFromCloud - onCancelled"
                        + databaseError.getDetails());
            }
        });
    }

    public interface GetAllTrackEntitiesFromFirebaseListener {
        void onAllTracksLoaded(List<TrackEntity> trackEntityList);
    }

    public void saveTrackToLocalDbFromCloud(final TrackEntity onlineTrackEntity) {
        MyLog.d(LOG_TAG, "saveTrackToLocalDbFromCloud");
        final String appUserId = FirebaseAuth.getInstance().getUid();
        if (appUserId == null) {
            return;
        }
        saveTrackEntityWithPointsToDbFromCloud(onlineTrackEntity, appUserId);
    }

    private void saveTrackEntityWithPointsToDbFromCloud(final TrackEntity onlineTrackEntity,
                                                        final String appUserId) {
        MyLog.d(LOG_TAG, "saveTrackEntityWithPointsToDbFromCloud");
        onlineTrackEntity.setTrackId(0);

        final DatabaseReference trackpointDbReference
                = FirebaseDatabase.getInstance().getReference()
                .child("trackpoints")
                .child(appUserId)
                .child(onlineTrackEntity.getFirebaseId());

        trackpointDbReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final List<TrackpointEntity> trackpointEntities = new ArrayList<>();

                for (DataSnapshot trackPointSnapshot : dataSnapshot.getChildren()) {
                    TrackpointEntity trackPointEntity
                            = trackPointSnapshot.getValue(TrackpointEntity.class);
                    trackpointEntities.add(trackPointEntity);
                }

                saveTrackEntityWithPointsToDb(onlineTrackEntity, trackpointEntities);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // TODO ?
            }
        });
    }

    @WorkerThread
    private void saveTrackEntityWithPointsToDb(final TrackEntity trackEntity,
                                               final List<TrackpointEntity> trackpointEntities) {
        discIoExecutor.execute(new Runnable() {
            @Override
            public void run() {
                final long trackId = trackDao.save(trackEntity);
                setTrackIdInTrackPointEntities(trackpointEntities, trackId);
                trackpointDao.saveAll(trackpointEntities);
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
        MyLog.d(LOG_TAG, "deleteTrackFromCloud");
        final String appUserId = FirebaseAuth.getInstance().getUid();
        if (appUserId == null) {
            return;
        }

        final DatabaseReference trackDbReference
                = FirebaseDatabase.getInstance().getReference().child("tracks")
                .child(appUserId)
                .child(firebaseId);
        trackDbReference.removeValue();
        final DatabaseReference trackpointsDbReference
                = FirebaseDatabase.getInstance().getReference()
                .child("trackpoints")
                .child(appUserId).child(firebaseId);
        trackpointsDbReference.removeValue();
    }

    public void deleteAllCloudData() {
        MyLog.d(LOG_TAG, "deleteAllCloudData");
        final DatabaseReference tracksDbReference
                = FirebaseDatabase.getInstance().getReference().child("tracks");
        tracksDbReference.removeValue();

        final DatabaseReference trackpointsDbReference
                = FirebaseDatabase.getInstance().getReference().child("trackpoints");
        trackpointsDbReference.removeValue();
    }
}
