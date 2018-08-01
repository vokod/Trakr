package com.awolity.trakr.repository;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;

import com.awolity.trakr.data.entity.TrackEntity;
import com.awolity.trakr.data.entity.TrackWithPoints;
import com.awolity.trakr.data.entity.TrackpointEntity;
import com.awolity.trakr.TrakrApplication;
import com.awolity.trakr.utils.Constants;
import com.awolity.trakr.utils.MyLog;
import com.awolity.trakr.utils.PreferenceUtils;
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

public class FirebaseTrackRepository {

    private static final String TAG = "FirebaseTrackRepository";

    @Inject
    Executor discIoExecutor;
    @Inject
    Context context;

    private DatabaseReference dbReference, userTracksReference, userTrackpointsReference;
    private String appUserId;

    public FirebaseTrackRepository() {
        TrakrApplication.getInstance().getAppComponent().inject(this);
        appUserId = FirebaseAuth.getInstance().getUid();
        if (appUserId == null) {
            return;
        }

        dbReference = FirebaseDatabase.getInstance().getReference();
        userTracksReference = dbReference.child(Constants.NODE_TRACKS).child(appUserId);
        userTrackpointsReference = dbReference.child(Constants.NODE_TRACKPOINTS).child(appUserId);
    }

    public void refreshReferences () { // TODO: ezt innen el. egyáltalán, kell ez?
        appUserId = FirebaseAuth.getInstance().getUid();
        if (appUserId == null) {
            return;
        }

        dbReference = FirebaseDatabase.getInstance().getReference();
        userTracksReference = dbReference.child(Constants.NODE_TRACKS).child(appUserId);
        userTrackpointsReference = dbReference.child(Constants.NODE_TRACKPOINTS).child(appUserId);
    }

    public String getIdForNewTrack() {
        if (appUserId == null) return null;
        return userTracksReference.child(appUserId).push().getKey();
    }

    @WorkerThread
    public void saveTrackToCloudOnThread(TrackWithPoints trackWithPoints, String trackFirebaseId) {
        refreshReferences();
        TrackEntity trackEntity = TrackEntity.fromTrackWithPoints(trackWithPoints);

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(Constants.NODE_TRACKS
                + "/"
                + appUserId
                + "/"
                + trackFirebaseId, trackEntity);
        childUpdates.put(Constants.NODE_TRACKPOINTS
                + "/"
                + appUserId
                + "/"
                + trackFirebaseId, trackWithPoints.getTrackPoints());

        dbReference.updateChildren(childUpdates);
    }

    public void updateTrackToCloud(TrackEntity trackEntity) {
        refreshReferences();
        final DatabaseReference trackDbReference
                = userTracksReference.child(trackEntity.getFirebaseId());

        trackDbReference.setValue(trackEntity);
    }

    public void getAllTrackEntitiesFromCloud(
            final TrackRepository.GetAllTrackEntitiesFromCloudListener listener) {
        refreshReferences();

        userTracksReference.addListenerForSingleValueEvent(new ValueEventListener() {
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
                MyLog.e(TAG, "Error in getAllTrackEntitiesFromCloud - onCancelled"
                        + databaseError.getDetails());
            }
        });
    }

    public void getTrackPoints(String firebaseTrackId,
                               final TrackRepository.GetTrackpointsFromCloudListener listener) {
        refreshReferences();

        final DatabaseReference trackpointDbReference
                = userTrackpointsReference.child(firebaseTrackId);

        trackpointDbReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final List<TrackpointEntity> trackpointEntities = new ArrayList<>();

                for (DataSnapshot trackPointSnapshot : dataSnapshot.getChildren()) {
                    TrackpointEntity trackPointEntity
                            = trackPointSnapshot.getValue(TrackpointEntity.class);
                    trackpointEntities.add(trackPointEntity);
                }
                listener.onTrackpointsLoaded(trackpointEntities);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // TODO ?
            }
        });
    }

    public void deleteTrackFromCloud(String firebaseTrackId) {
        refreshReferences();
        // remove track from track node
        userTracksReference.child(firebaseTrackId).removeValue();
        // remove track from trackpoint node
        userTrackpointsReference.child(firebaseTrackId).removeValue();
    }
}