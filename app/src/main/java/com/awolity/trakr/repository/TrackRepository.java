package com.awolity.trakr.repository;

import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;

import com.awolity.trakr.data.dao.TrackDao;
import com.awolity.trakr.data.dao.TrackpointDao;
import com.awolity.trakr.data.entity.TrackEntity;
import com.awolity.trakr.data.entity.TrackWithPoints;
import com.awolity.trakr.data.entity.TrackpointEntity;
import com.awolity.trakr.di.TrakrApplication;
import com.awolity.trakr.gpx.GpxExporter;
import com.awolity.trakr.utils.MyLog;
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
    private long id;

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

    @WorkerThread
    public List<TrackEntity> getTracksSync() {
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
                if(!entity.getFirebaseId().isEmpty()){
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
    public void saveTrackToFirebase(final long trackId) {

        discIoExecutor.execute(new Runnable() {
            @Override
            public void run() {
                TrackWithPoints trackWithPoints = trackDao.loadByIdWithPointsSync(trackId);
                TrackEntity entity = TrackEntity.fromTrackWithPoints(trackWithPoints);

                final DatabaseReference dbReference
                        = FirebaseDatabase.getInstance().getReference();
                String trackFirebaseId = dbReference.child("tracks").push().getKey();

                trackWithPoints.setFirebaseId(trackFirebaseId);
                trackDao.update(entity);

                Map<String, Object> childUpdates = new HashMap<>();
                // create chat in "chatsLiveData" node
                childUpdates.put("tracks"
                        + "/"
                        + trackFirebaseId, entity);
                childUpdates.put("trackpoints"
                        + "/"
                        + trackFirebaseId, trackWithPoints.getTrackPoints());
                dbReference.updateChildren(childUpdates);
            }
        });
    }

    public void getAllTrackEntitiesFromFirebase(final GetAllTrackEntitiesFromFirebaseListener listener) {
        final DatabaseReference tracksDbReference
                = FirebaseDatabase.getInstance().getReference().child("tracks");

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
                MyLog.e(LOG_TAG, "Error in getAllTrackEntitiesFromFirebase - onCancelled" + databaseError.getDetails());
            }
        });
    }

    public interface GetAllTrackEntitiesFromFirebaseListener {
        void onAllTracksLoaded(List<TrackEntity> trackEntityList);
    }

    public void saveTrackToLocalDbFromFirebase(final String firebaseId){
        final DatabaseReference trackDbReference
                = FirebaseDatabase.getInstance().getReference().child("tracks").child(firebaseId);

        trackDbReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                TrackEntity entity = dataSnapshot.getValue(TrackEntity.class);
                entity.setTrackId(0);
                final long trackId = trackDao.save(entity);

                final DatabaseReference trackpointDbReference
                        = FirebaseDatabase.getInstance().getReference().child("trackpoints").child(firebaseId);

                trackpointDbReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        List <TrackpointEntity> trackpointEntities = new ArrayList<>();
                        for(DataSnapshot trackPointSnapshot : dataSnapshot.getChildren()){
                            TrackpointEntity trackPointEntity = trackPointSnapshot.getValue(TrackpointEntity.class);
                            trackPointEntity.setTrackId(trackId);
                            trackpointEntities.add(trackPointEntity);
                        }
                        trackpointDao.saveAll(trackpointEntities);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void deleteTrackFromCloud(String firebaseId){
        final DatabaseReference trackDbReference
                = FirebaseDatabase.getInstance().getReference().child("tracks").child(firebaseId);
        trackDbReference.removeValue();
        final DatabaseReference trackpointsDbReference
                = FirebaseDatabase.getInstance().getReference().child("trackpoints").child(firebaseId);
        trackpointsDbReference.removeValue();
    }
}
