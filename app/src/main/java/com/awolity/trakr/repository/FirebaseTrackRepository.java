package com.awolity.trakr.repository;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;

import com.awolity.trakr.TrakrApplication;
import com.awolity.trakr.data.entity.TrackEntity;
import com.awolity.trakr.data.entity.TrackWithPoints;
import com.awolity.trakr.data.entity.TrackpointEntity;
import com.awolity.trakr.utils.Constants;
import com.awolity.trakr.utils.MyLog;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
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

    String getIdForNewTrack() {
        if (appUserId == null) return null;
        return userTracksReference.document().getId();
    }

    @WorkerThread
    void saveTrackToCloudOnThread(TrackWithPoints trackWithPoints, String trackFirebaseId) {
        MyLog.d(TAG, "saveTrackToCloudOnThread() called with: trackWithPoints = [" + trackWithPoints + "], trackFirebaseId = [" + trackFirebaseId + "]");
        refreshReferences();
        TrackEntity trackEntity = TrackEntity.fromTrackWithPoints(trackWithPoints);

        DocumentReference trackReference = userTracksReference.document(trackFirebaseId);
        trackReference.set(trackEntity);
        CollectionReference pointsReference = trackReference.collection(Constants.COLLECTION_TRACKPOINTS);

        int batchesNumber = trackWithPoints.getTrackPoints().size() / Constants.BATCH;
        for (int i = 0; i <= batchesNumber; i++) {
            int start = i * Constants.BATCH;
            int end = (i + 1) * Constants.BATCH;
            if (end > trackWithPoints.getTrackPoints().size()) {
                end = trackWithPoints.getTrackPoints().size();
            }
            writeBatch(pointsReference, start, end, trackWithPoints.getTrackPoints());
        }
    }

    @WorkerThread
    private void writeBatch(CollectionReference pointsReference, int start, int end, List<TrackpointEntity> points) {
        MyLog.d(TAG, "writeBatch() called with: pointsReference = [" + pointsReference + "], start = [" + start + "], end = [" + end + "], points = [" + points + "]");
        WriteBatch batch = FirebaseFirestore.getInstance().batch();
        for (int i = start; i < end; i++) {
            TrackpointEntity point = points.get(i);
            pointsReference.document(String.valueOf(i)).set(point);
        }
        try {
            Tasks.await(batch.commit());
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    void updateTrackToCloud(TrackEntity trackEntity) {
        refreshReferences();
        final DocumentReference trackReference
                = userTracksReference.document(trackEntity.getFirebaseId());
        trackReference.set(trackEntity);
    }

    void getAllTrackEntitiesFromCloud(
            final TrackRepository.GetAllTrackEntitiesFromCloudListener listener) {
        refreshReferences();
        userTracksReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<TrackEntity> trackEntities = new ArrayList<>();
                for (QueryDocumentSnapshot doc : task.getResult()) {
                    trackEntities.add(doc.toObject(TrackEntity.class));
                }
                listener.onAllTracksLoaded(trackEntities);
            } else {
                MyLog.e(TAG, "Error in getAllTrackEntitiesFromCloud "
                        + task.getException().getLocalizedMessage());
            }
        });
    }

    void getTrackPoints(String firebaseTrackId,
                        final TrackRepository.GetTrackpointsFromCloudListener listener) {
        refreshReferences();

        final CollectionReference pointsReference = userTracksReference
                .document(firebaseTrackId)
                .collection(Constants.COLLECTION_TRACKPOINTS);

        pointsReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                final List<TrackpointEntity> points = new ArrayList<>();
                for (QueryDocumentSnapshot doc : task.getResult()) {
                    points.add(doc.toObject(TrackpointEntity.class));
                }
                listener.onTrackpointsLoaded(points);
            } else {
                MyLog.e(TAG, "Error in getTrackPoints "
                        + task.getException().getLocalizedMessage());
            }
        });
    }

    void deleteTrackFromCloud(String firebaseTrackId) {
        refreshReferences();
        FirebaseFunctions functions = FirebaseFunctions.getInstance();
        Map<String, Object> data = new HashMap<>();
        data.put("path", Constants.COLLECTION_USERS + "/" + appUserId + "/" + firebaseTrackId);
        functions.getHttpsCallable("recursiveDelete")
                .call(data)
                .continueWith(task -> {
                    // This continuation runs on either success or failure, but if the task
                    // has failed then getResult() will throw an Exception which will be
                    // propagated down.
                    String result = (String) task.getResult().getData();
                    MyLog.d(TAG, result);
                    return result;
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
