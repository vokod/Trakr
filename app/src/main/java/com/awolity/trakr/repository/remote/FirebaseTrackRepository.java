package com.awolity.trakr.repository.remote;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;

import com.awolity.trakr.TrakrApplication;
import com.awolity.trakr.data.entity.TrackEntity;
import com.awolity.trakr.data.entity.TrackWithPoints;
import com.awolity.trakr.data.entity.TrackpointEntity;
import com.awolity.trakr.repository.AppUserRepository;
import com.awolity.trakr.repository.TrackRepository;
import com.awolity.trakr.repository.remote.model.ConvertersKt;
import com.awolity.trakr.repository.remote.model.FirestoreTrack;
import com.awolity.trakr.utils.Constants;
import com.awolity.trakr.utils.MyLog;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.functions.FirebaseFunctions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

@Singleton
public class FirebaseTrackRepository {

    @Inject
    @Named("disc")
    Executor discIoExecutor;
    @Inject
    Context context;
    @SuppressWarnings("WeakerAccess")
    @Inject
    AppUserRepository appUserRepository;

    private static final String TAG = "FirebaseTrackRepository";
    private DocumentReference userReference;
    private CollectionReference userTracksReference;
    private String appUserId;

    public FirebaseTrackRepository() {
        TrakrApplication.getInstance().getAppComponent().inject(this);
        refreshReferences();
    }

    public String getIdForNewTrack() {
        if (appUserId == null) return null;
        return userTracksReference.document().getId();
    }

    @WorkerThread
    public void saveTrackToCloudOnThread(TrackWithPoints trackWithPoints, String trackFirebaseId) {
        MyLog.d(TAG, "saveTrackToCloudOnThread() called with: trackWithPoints = [" + trackWithPoints + "], trackFirebaseId = [" + trackFirebaseId + "]");
        refreshReferences();
        FirestoreTrack firestoreTrack = ConvertersKt.trackWithpointsToFirestoreTrack(trackWithPoints);
        DocumentReference trackReference = userTracksReference.document(trackFirebaseId);
        trackReference.set(firestoreTrack);
    }

    public void updateTrackToCloud(TrackEntity trackEntity) {
        refreshReferences();
        FirestoreTrack firestoreTrack = ConvertersKt.trackEntityToFirestoreTrack(trackEntity);
        final DocumentReference trackReference
                = userTracksReference.document(firestoreTrack.getFirebaseId());
        trackReference.set(firestoreTrack);
    }

    public void getAllTracksFromCloud(
            final TrackRepository.GetAllTracksFromCloudListener listener) {
        refreshReferences();
        userTracksReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<TrackWithPoints> tracksWithPoints = new ArrayList<>();
                for (QueryDocumentSnapshot doc : task.getResult()) {
                    FirestoreTrack firestoreTrack = doc.toObject(FirestoreTrack.class);
                    tracksWithPoints.add(ConvertersKt.firestoreTrackToTrackWithPoints(firestoreTrack));
                }
                listener.onAllTracksLoaded(tracksWithPoints);
            } else {
                MyLog.e(TAG, "Error in getAllTracksFromCloud "
                        + task.getException().getLocalizedMessage());
            }
        });
    }

    public void deleteTrackFromCloud(String firebaseTrackId) {
        refreshReferences();
        userTracksReference.document(firebaseTrackId).delete()
                .addOnSuccessListener(aVoid -> {
                    MyLog.d(TAG, "deleteTrackFromCloud - success");
                })
                .addOnFailureListener(e -> {
                    MyLog.e(TAG, "deleteTrackFromCloud - error: " + e.getLocalizedMessage());
                });
    }

    private void refreshReferences() {
        appUserId = appUserRepository.getAppUserId();
        if (appUserId == null) {
            return;
        }
        userReference = FirebaseFirestore.getInstance()
                .collection(Constants.COLLECTION_USERS).document(appUserId);
        userTracksReference = userReference.collection(Constants.COLLECTION_TRACKS);
    }
}
