package com.awolity.trakr.sync;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.WorkerThread;
import android.widget.Toast;

import com.awolity.trakr.R;
import com.awolity.trakr.data.entity.TrackEntity;
import com.awolity.trakr.TrakrApplication;
import com.awolity.trakr.repository.TrackRepository;
import com.awolity.trakr.utils.MyLog;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

import javax.inject.Inject;

public class SyncService extends IntentService {

    private static final String LOG_TAG = SyncService.class.getSimpleName();

    @Inject // TODO: ide beinjektálni az AppUserRepo-t és onnan szedni a releváns infókat
            TrackRepository trackRepository;
    @Inject
    Executor discIoExecutor;

    public SyncService() {
        super("SyncService");
        MyLog.d(LOG_TAG, "SyncService");
        TrakrApplication.getInstance().getAppComponent().inject(this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        MyLog.d(LOG_TAG, "onHandleIntent");
        if (FirebaseAuth.getInstance().getUid() == null) {
            MyLog.d(LOG_TAG, "onHandleIntent - user not logged in, no sync :(");
            return;
        }

        if (!isConnected(this)) {
            Toast.makeText(this, getString(R.string.sync_service_no_net),
                    Toast.LENGTH_LONG).show();
            // TODO: refactor to snackbar
            return;
        }

        trackRepository.getAllTrackEntitiesFromCloud(
                new TrackRepository.GetAllTrackEntitiesFromCloudListener() {
                    @Override
                    public void onAllTracksLoaded(final List<TrackEntity> onlineTracks) {
                        discIoExecutor.execute(new Runnable() {
                            @Override
                            public void run() {
                                List<TrackEntity> offlineTracks = trackRepository.getTracksSync();
                                List<TrackEntity> onlyOfflineTracks = new ArrayList<>();
                                // get those tracks from offline tracks, that has no firebaseId
                                // those are only offline tracks
                                // get those tracks from offline tracks that are already backed up to cloud
                                // List<TrackEntity> cloudSavedOfflineTracks = new ArrayList<>();
                                for (TrackEntity trackEntity : offlineTracks) {
                                    if (trackEntity.getFirebaseId() == null || trackEntity.getFirebaseId().isEmpty()) {
                                        onlyOfflineTracks.add(trackEntity);
                                    } /*else {
                                        cloudSavedOfflineTracks.add(trackEntity);
                                    }*/
                                }

                                // add those tracks from the online tracks
                                // that are not present locally
                                // to a list
                                // the TrackEntity objects are not equal, they have different id-s, so compare start time
                                List<TrackEntity> onlyOnlineTracks = new ArrayList<>();
                                for (TrackEntity onlineTrack : onlineTracks) {
                                    boolean isOnlyOnline = true;
                                    for (TrackEntity offlineTrack : offlineTracks) {
                                        if (onlineTrack.getStartTime() == offlineTrack.getStartTime()) {
                                            // track is present locally, move to next
                                            isOnlyOnline = false;
                                            break;
                                        }
                                    }
                                    if (isOnlyOnline) {
                                        onlyOnlineTracks.add(onlineTrack);
                                    }
                                }

                                // add those tracks from local tracks
                                // that has a firebaseId (was uploaded earlier)
                                // but are not among the online tracks (the user deleted them using another device)
                                List<TrackEntity> cloudDeletedOfflineTracks = new ArrayList<>();
                                for (TrackEntity offlineTrack : offlineTracks) {
                                    boolean isCloudDeleted = true;
                                    if (offlineTrack.getFirebaseId() == null
                                            || offlineTrack.getFirebaseId().isEmpty()) {
                                        continue;
                                    }
                                    for (TrackEntity onlineTrack : onlineTracks) {
                                        if (onlineTrack.getStartTime() == offlineTrack.getStartTime()) {
                                            // track is present online, remove it
                                            isCloudDeleted = false;
                                            break;
                                        }
                                    }
                                    if (isCloudDeleted) {
                                        cloudDeletedOfflineTracks.add(offlineTrack);
                                    }
                                }

                                // this saves tracks that are present in cloud but not in the device
                                saveOnlyOnlineTracksToDb(onlyOnlineTracks);
                                // this uploads the tracks that were recorded on this device and were not yet uploaded
                                uploadOfflineTracks(onlyOfflineTracks);
                                // this delete the local tracks that were deleted from the cloud in another installation
                                deleteCloudDeletedTracks(cloudDeletedOfflineTracks);
                            }
                        });
                    }
                });

        new DbSanitizer().sanitizeDb();
    }

    private void saveOnlyOnlineTracksToDb(List<TrackEntity> onlyOnlineTracks) {
        for (TrackEntity onlineTrack : onlyOnlineTracks) {
            trackRepository.saveTrackToLocalDbFromCloud(onlineTrack);
        }
    }

    @WorkerThread
    private void uploadOfflineTracks(List<TrackEntity> onlyOfflineTracks) {
        MyLog.d(LOG_TAG, "uploadOfflineTracks");
        for (TrackEntity trackEntity : onlyOfflineTracks) {
            trackRepository.saveTrackToCloudOnThread(trackEntity.getTrackId());
        }
    }

    @WorkerThread
    private void deleteCloudDeletedTracks(List<TrackEntity> cloudSavedOfflineTracks) {
        MyLog.d(LOG_TAG, "deleteCloudDeletedTracks");

        for (TrackEntity cloudDeletedOfflineTrack : cloudSavedOfflineTracks) {
            trackRepository.deleteLocalTrack(cloudDeletedOfflineTrack.getTrackId());
        }
    }

    private static boolean isConnected(Context context) {
        MyLog.d(LOG_TAG, "isConnected");
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }
}

