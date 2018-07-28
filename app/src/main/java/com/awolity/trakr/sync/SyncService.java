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

    @Inject
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
                                List<TrackEntity> cloudSavedOfflineTracks = new ArrayList<>();
                                for (TrackEntity trackEntity : offlineTracks) {
                                    if (trackEntity.getFirebaseId().isEmpty()) {
                                        onlyOfflineTracks.add(trackEntity);
                                    } else {
                                        cloudSavedOfflineTracks.add(trackEntity);
                                    }
                                }

                                // this saves tracks that are present in cloud but not in the device
                                saveOnlineTracks(onlineTracks, offlineTracks);
                                // this uploads the tracks that were recorded on this device and were not yet uploaded
                                uploadOfflineTracks(onlyOfflineTracks);
                                // this delete the local tracks that were deleted from the cloud in another installation
                                deleteCloudDeletedTracks(cloudSavedOfflineTracks, onlineTracks);
                            }
                        });
                    }
                });

        new DbSanitizer().sanitizeDb();
    }

    private void saveOnlineTracks(List<TrackEntity> onlineTrackEntities,
                                  List<TrackEntity> offlineTrackEntities) {
        // remove those tracks from the downloaded tracks,
        // that are present offline.
        // the TrackEntity objects are not equal, they have different id-s, so compare start time
        for (TrackEntity offlineTrack : offlineTrackEntities) {
            for (TrackEntity onlineTrack : onlineTrackEntities) {
                if (onlineTrack.getStartTime() == offlineTrack.getStartTime()) {
                    onlineTrackEntities.remove(onlineTrack);
                    break;
                }
            }
        }

        // save the remaining tracks
        for (TrackEntity onlineTrack : onlineTrackEntities) {
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
    private void deleteCloudDeletedTracks(List<TrackEntity> cloudSavedTracks,
                                          List<TrackEntity> onlineTracks) {
        MyLog.d(LOG_TAG, "deleteCloudDeletedTracks");
        for (TrackEntity cloudSavedTrack : cloudSavedTracks) {
            for (TrackEntity onlineTrack : onlineTracks) {
                if (onlineTrack.getStartTime() == cloudSavedTrack.getStartTime()) {
                    cloudSavedTracks.remove(onlineTrack);
                    break;
                }
            }
        }
        for (TrackEntity cloudDeletedOfflineTrack : cloudSavedTracks) {
            trackRepository.deleteTrack(cloudDeletedOfflineTrack.getTrackId());
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

