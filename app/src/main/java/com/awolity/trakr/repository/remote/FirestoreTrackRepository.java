package com.awolity.trakr.repository.remote;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.awolity.trakr.TrakrApplication;
import com.awolity.trakr.data.entity.TrackEntity;
import com.awolity.trakr.data.entity.TrackWithPoints;
import com.awolity.trakr.repository.AppUserRepository;
import com.awolity.trakr.repository.TrackRepository;
import com.awolity.trakr.repository.remote.model.ConvertersKt;
import com.awolity.trakr.repository.remote.model.FirestoreTrack;
import com.awolity.trakr.repository.remote.model.FirestoreTrackData;
import com.awolity.trakr.repository.remote.model.PointDistances;
import com.awolity.trakr.utils.Constants;
import com.awolity.trakr.utils.MyLog;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.Transaction;

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

        FirestoreTrack firestoreTrack = ConvertersKt.trackWithPointsToFirestoreTrack(trackWithPoints);
        final DocumentReference trackReference = userTracksReference.document(trackFirebaseId);
        trackReference.set(firestoreTrack)
               /* .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            DocumentReference distancesRef = userTracksReference
                                    .document(trackFirebaseId)
                                    .collection(Constants.COLLECTION_POINTS)
                                    .document(Constants.DOCUMENT_DISTANCES);
                            PointDistances pd = new PointDistances(
                                    ConvertersKt.trackPointsToDistancesList(
                                            trackWithPoints.getTrackPoints()));
                            distancesRef.set(pd);
                        } else {
                            MyLog.e(TAG, "saveTrackToCloud - error: " + task.getException());
                        }
                    }
                })*/;

        final DocumentReference trackDataReference = userTrackdatasReference.document(trackFirebaseId);
        trackDataReference.set(
                ConvertersKt.trackEntityToFirestoreTrackData(trackWithPoints.getTrackEntity()));
    }

    public void updateTrackTitleToCloud(String firebaseId, String title) {
        refreshReferences();
        final DocumentReference trackReference = userTracksReference.document(firebaseId);
        final DocumentReference trackDataReference = userTrackdatasReference.document(firebaseId);

        trackReference.update("t", title);
        trackDataReference.update("t", title);
    }

    public void getAllTracksFromCloud(
            final TrackRepository.GetAllTracksFromCloudListener listener) {
        refreshReferences();
        userTracksReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<TrackWithPoints> tracksWithPoints = new ArrayList<>();
                if (task.getResult() == null) return;
                for (QueryDocumentSnapshot doc : task.getResult()) {
                    FirestoreTrack firestoreTrack = doc.toObject(FirestoreTrack.class);
                    tracksWithPoints.add(ConvertersKt.firestoreTrackToTrackWithPoints(firestoreTrack));
                }
                listener.onAllTracksLoaded(tracksWithPoints);
            } else {
                if (task.getException() == null) return;
                MyLog.e(TAG, "Error in getAllTracksFromCloud "
                        + task.getException().getLocalizedMessage());
            }
        });
    }

    public void getAllTrackDatasFromCloud(
            final TrackRepository.GetAllTrackDatasFromCloudListener listener) {
        refreshReferences();
        userTrackdatasReference.get().addOnCompleteListener(task -> {
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
                MyLog.e(TAG, "Error in getAllTracksFromCloudWithoutPoints "
                        + task.getException().getLocalizedMessage());
            }
        });
    }

    public void getTrackFromCloud(final String id,
                                  final TrackRepository.GetTrackFromCloudListener listener) {
        refreshReferences();
        userTracksReference.document(id).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult() == null) return;
                FirestoreTrack track = task.getResult().toObject(FirestoreTrack.class);
                if (track == null) return;
                listener.onTrackLoaded(ConvertersKt.firestoreTrackToTrackWithPoints(track));
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
