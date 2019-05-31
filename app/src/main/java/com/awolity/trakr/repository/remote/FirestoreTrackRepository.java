package com.awolity.trakr.repository.remote;

import android.content.Context;

import com.awolity.trakr.TrakrApplication;
import com.awolity.trakr.data.entity.TrackEntity;
import com.awolity.trakr.data.entity.TrackWithPoints;
import com.awolity.trakr.repository.AppUserRepository;
import com.awolity.trakr.repository.TrackRepository;
import com.awolity.trakr.repository.remote.model.ConvertersKt;
import com.awolity.trakr.repository.remote.model.FirestoreTrackData;
import com.awolity.trakr.repository.remote.model.PointAltitudes;
import com.awolity.trakr.repository.remote.model.PointDistances;
import com.awolity.trakr.repository.remote.model.PointGeopoints;
import com.awolity.trakr.repository.remote.model.PointSpeeds;
import com.awolity.trakr.repository.remote.model.PointTimes;
import com.awolity.trakr.utils.Constants;
import com.awolity.trakr.utils.MyLog;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

@Singleton
public class FirestoreTrackRepository {

    @Inject
    @Named("disc")
    Executor discIoExecutor;
    @Inject
    Context context;
    @Inject
    AppUserRepository appUserRepository;

    private static final String TAG = "FirestoreTrackRepository";
    private CollectionReference userTracksReference;
    private CollectionReference userTrackdatasReference;
    private String appUserId;

    public FirestoreTrackRepository() {
        TrakrApplication.getInstance().getAppComponent().inject(this);
        refreshReferences();
    }

    public String getIdForNewTrack() {
        if (appUserId == null) return null;
        return userTracksReference.document().getId();
    }

    public void saveTrackToCloud(TrackWithPoints trackWithPoints, String trackFirebaseId) {
        refreshReferences();

        FirestoreTrackData trackData = ConvertersKt.trackWithPointsToFirestoreTrackData(trackWithPoints);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        WriteBatch batch = db.batch();

        final DocumentReference trackReference = userTracksReference.document(trackFirebaseId);
        batch.set(trackReference, trackData);

        final DocumentReference timesRef = trackReference.collection(Constants.COLLECTION_POINTS)
                .document(Constants.DOCUMENT_TIMES);
        batch.set(timesRef, ConvertersKt.trackPointsToTimesList(trackWithPoints.getTrackPoints()));

        final DocumentReference speedsRef = trackReference.collection(Constants.COLLECTION_POINTS)
                .document(Constants.DOCUMENT_SPEEDS);
        batch.set(speedsRef, ConvertersKt.trackPointsToSpeedList(trackWithPoints.getTrackPoints()));

        final DocumentReference altitudesRef = trackReference.collection(Constants.COLLECTION_POINTS)
                .document(Constants.DOCUMENT_ALTITUDES);
        batch.set(altitudesRef, ConvertersKt.trackPointsToAltitudeList(trackWithPoints.getTrackPoints()));

        final DocumentReference geopointsRef = trackReference.collection(Constants.COLLECTION_POINTS)
                .document(Constants.DOCUMENT_GEOPOINTS);
        batch.set(geopointsRef, ConvertersKt.trackPointsToGeopointList(trackWithPoints.getTrackPoints()));

        final DocumentReference distancesRef = trackReference.collection(Constants.COLLECTION_POINTS)
                .document(Constants.DOCUMENT_DISTANCES);
        batch.set(distancesRef, ConvertersKt.trackPointsToDistancesList(trackWithPoints.getTrackPoints()));

        batch.commit().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                MyLog.d(TAG, "saveTrackToCloud - success");
            } else {
                MyLog.e(TAG, "saveTrackToCloud - error:" + task.getException());
            }
        });
    }

    public void updateTrackTitleToCloud(String firebaseId, String title) {
        refreshReferences();
        final DocumentReference trackReference = userTracksReference.document(firebaseId);
        final DocumentReference trackDataReference = userTrackdatasReference.document(firebaseId);

        trackReference.update("t", title);
        trackDataReference.update("t", title);
    }

    public void getAllTrackDatasFromCloud(
            final TrackRepository.GetAllTrackDatasFromCloudListener listener) {
        refreshReferences();
        userTracksReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<TrackEntity> tracks = new ArrayList<>();
                if (task.getResult() == null) return;
                for (QueryDocumentSnapshot doc : task.getResult()) {
                    FirestoreTrackData firestoreTrackData = doc.toObject(FirestoreTrackData.class);
                    tracks.add(ConvertersKt.firestoreTrackDataToTrackEntity(firestoreTrackData));
                }
                listener.onAllTrackdatasLoaded(tracks);
            } else {
                if (task.getException() == null) return;
                MyLog.e(TAG, "Error in getAllTrackDatasFromCloud "
                        + task.getException().getLocalizedMessage());
            }
        });
    }

    public void getTrackPointsFromCloud(final TrackEntity track,
                                        final TrackRepository.GetTrackPointsFromCloudListener listener) {
        refreshReferences();
        final TrackWithPoints trackWithPoints = new TrackWithPoints();
        trackWithPoints.setTrackEntity(track);
        userTracksReference.document(track.getFirebaseId()).collection(Constants.COLLECTION_POINTS).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        PointAltitudes altitudes = null;
                        PointDistances distances = null;
                        PointTimes times = null;
                        PointGeopoints geopoints = null;
                        PointSpeeds speeds = null;
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            switch (document.getId()) {
                                case Constants.DOCUMENT_ALTITUDES: //altitude
                                    altitudes = document.toObject(PointAltitudes.class);
                                    break;
                                case Constants.DOCUMENT_DISTANCES: //distance
                                    distances = document.toObject(PointDistances.class);
                                    break;
                                case Constants.DOCUMENT_TIMES: //time
                                    times = document.toObject(PointTimes.class);
                                    break;
                                case Constants.DOCUMENT_GEOPOINTS: //points
                                    geopoints = document.toObject(PointGeopoints.class);
                                    break;
                                case Constants.DOCUMENT_SPEEDS: //speed
                                    speeds = document.toObject(PointSpeeds.class);
                                    break;
                            }
                        }
                        trackWithPoints.setTrackPoints(ConvertersKt.firestorePointsToTrackPointEntities(
                                altitudes, distances, geopoints, speeds, times));
                        listener.onTrackLoaded(trackWithPoints);

                    } else {
                        if (task.getException() == null) return;
                        MyLog.e(TAG, "Error in getTrackPointsFromCloud "
                                + task.getException().getLocalizedMessage());
                    }
                });
    }

    public void getTrackDataFromCloud(final String id,
                                      final TrackRepository.GetTrackDataFromCloudListener listener) {
        refreshReferences();
        userTracksReference.document(id).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult() == null) return;
                FirestoreTrackData track = task.getResult().toObject(FirestoreTrackData.class);
                if (track == null) return;
                listener.onTrackLoaded(ConvertersKt.firestoreTrackDataToTrackEntity(track));
            } else {
                if (task.getException() == null) return;
                MyLog.e(TAG, "Error in getTrackFromCloud "
                        + task.getException().getLocalizedMessage());
            }
        });
    }

    public void deleteTrackFromCloud(String firebaseTrackId) {
        refreshReferences();
        userTracksReference.document(firebaseTrackId).delete()
                .addOnSuccessListener(aVoid -> MyLog.d(TAG, "deleteTrackFromCloud - success"))
                .addOnFailureListener(e -> MyLog.e(TAG, "deleteTrackFromCloud - error: " + e.getLocalizedMessage()));

        userTrackdatasReference.document(firebaseTrackId).delete()
                .addOnSuccessListener(aVoid -> MyLog.d(TAG, "deleteTrackFromCloud - success"))
                .addOnFailureListener(e -> MyLog.e(TAG, "deleteTrackFromCloud - error: " + e.getLocalizedMessage()));
    }

    private void refreshReferences() {
        appUserId = appUserRepository.getAppUserId();
        if (appUserId == null) {
            return;
        }
        DocumentReference userReference = FirebaseFirestore.getInstance()
                .collection(Constants.COLLECTION_USERS).document(appUserId);
        userTracksReference = userReference.collection(Constants.COLLECTION_TRACKS);
        userTrackdatasReference = userReference.collection(Constants.COLLECTION_TRACKDATAS);
    }
}
