package com.awolity.trakr.sync;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import androidx.annotation.WorkerThread;
import android.widget.Toast;

import com.awolity.trakr.R;
import com.awolity.trakr.TrakrApplication;
import com.awolity.trakr.data.entity.TrackEntity;
import com.awolity.trakr.data.entity.TrackWithPoints;
import com.awolity.trakr.repository.AppUserRepository;
import com.awolity.trakr.repository.TrackRepository;
import com.awolity.trakr.utils.MyLog;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

@Singleton
public class SyncService extends IntentService {

    private static final String TAG = SyncService.class.getSimpleName();

    @SuppressWarnings("WeakerAccess")
    @Inject
    TrackRepository trackRepository;

    @SuppressWarnings("WeakerAccess")
    @Inject
    @Named("disc")
    Executor discIoExecutor;

    @SuppressWarnings("WeakerAccess")
    @Inject
    AppUserRepository appUserRepository;

    public SyncService() {
        super("SyncService");
        // MyLog.d(TAG, "SyncService");
        TrakrApplication.getInstance().getAppComponent().inject(this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // MyLog.d(TAG, "onHandleIntent");
        if (!appUserRepository.IsAppUserLoggedIn()) {
            // MyLog.d(TAG, "onHandleIntent - user not logged in, no sync :(");
            return;
        }

        if (!isConnected(this)) {
            Toast.makeText(this, getString(R.string.sync_service_no_net),
                    Toast.LENGTH_LONG).show();
            // TODO: refactor to snackbar
            return;
        }

        trackRepository.getAllTracksFromCloud(
                onlineTracks -> discIoExecutor.execute(() -> {
                    List<TrackEntity> offlineTracks = trackRepository.getTracksSync();
                    List<TrackEntity> onlyOfflineTracks = getOnyOfflineTracks(
                            offlineTracks);
                    List<TrackWithPoints> onlyOnlineTracks = getOnyOnlineTracks(
                            onlineTracks, offlineTracks);
                    List<TrackEntity> cloudDeletedOfflineTracks
                            = getCloudDeletedOfflineTracks(onlineTracks, offlineTracks);

                    // this saves tracks that are present in cloud but not in the device
                    saveOnlyOnlineTracksToDb(onlyOnlineTracks);
                    // this uploads the tracks that were recorded on this device and were not yet uploaded
                    uploadOfflineTracks(onlyOfflineTracks);
                    // this delete the local tracks that were deleted from the cloud in another installation
                    deleteCloudDeletedTracks(cloudDeletedOfflineTracks);
                    // check if there are tracks that's titles are changed locally, then update them
                    checkForNameChanges(onlineTracks, offlineTracks);
                }));

        new DbSanitizer().sanitizeDb();
    }

    private List<TrackEntity> getOnyOfflineTracks(List<TrackEntity> offlineTracks) {
        List<TrackEntity> onlyOfflineTracks = new ArrayList<>();
        // get those tracks from offline tracks, that has no firebaseId
        // those are only offline tracks
        // get those tracks from offline tracks that are already backed up to cloud
        // List<TrackEntity> cloudSavedOfflineTracks = new ArrayList<>();
        for (TrackEntity trackEntity : offlineTracks) {
            if (trackEntity.getFirebaseId() == null || trackEntity.getFirebaseId().isEmpty()) {
                onlyOfflineTracks.add(trackEntity);
            }
        }
        return onlyOfflineTracks;
    }

    private List<TrackWithPoints> getOnyOnlineTracks(List<TrackWithPoints> onlineTracks,
                                                 List<TrackEntity> offlineTracks) {
        // add those tracks from the online tracks
        // that are not present locally
        // to a list
        // the TrackEntity objects are not equal, they have different id-s, so compare start time
        List<TrackWithPoints> onlyOnlineTracks = new ArrayList<>();
        for (TrackWithPoints onlineTrack : onlineTracks) {
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
        return onlyOnlineTracks;
    }

    private void checkForNameChanges(List<TrackWithPoints> onlineTracks,
                                     List<TrackEntity> offlineTracks) {
        // get the offline version of every online track
        // then check if their titles are the same
        // if not, then save the online title
        for (TrackEntity onlineTrack : onlineTracks) {
            for (TrackEntity offlineTrack : offlineTracks) {
                if (onlineTrack.getStartTime() == offlineTrack.getStartTime()) {
                    // track is present locally, check titles
                    if (!onlineTrack.getTitle().equals(offlineTrack.getTitle())) {
                        offlineTrack.setTitle(onlineTrack.getTitle());
                        trackRepository.updateTrackInDb(offlineTrack);
                    }
                    break;
                }
            }
        }
    }

    private List<TrackEntity> getCloudDeletedOfflineTracks(List<TrackWithPoints> onlineTracks,
                                                           List<TrackEntity> offlineTracks) {
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
        return cloudDeletedOfflineTracks;
    }

    private void saveOnlyOnlineTracksToDb(List<TrackWithPoints> onlyOnlineTracks) {
        MyLog.d(TAG, "saveOnlyOnlineTracksToDb");
        for (TrackWithPoints onlineTrack : onlyOnlineTracks) {
            MyLog.d(TAG, "saveOnlyOnlineTracksToDb - saving online track: " + onlineTrack.getFirebaseId());
            trackRepository.saveTrackToLocalDbFromCloud(onlineTrack);
        }
    }

    @WorkerThread
    private void uploadOfflineTracks(List<TrackEntity> onlyOfflineTracks) {
        // MyLog.d(TAG, "uploadOfflineTracks");
        for (TrackEntity trackEntity : onlyOfflineTracks) {
            trackRepository.saveTrackToCloudOnThread(trackEntity.getTrackId());
        }
    }

    @WorkerThread
    private void deleteCloudDeletedTracks(List<TrackEntity> cloudSavedOfflineTracks) {
        // MyLog.d(TAG, "deleteCloudDeletedTracks");
        for (TrackEntity cloudDeletedOfflineTrack : cloudSavedOfflineTracks) {
            trackRepository.deleteLocalTrack(cloudDeletedOfflineTrack.getTrackId());
        }
    }

    private static boolean isConnected(Context context) {
        // MyLog.d(TAG, "isConnected");
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }
}

