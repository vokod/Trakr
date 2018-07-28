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

import java.util.Iterator;
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

        if (isConnected(this)) {
            setInstallationId();
            // this downloads tracks that are present in cloud but not in the device
            downloadOnlineTracks();
            // this uploads the tracks that were recorded on this device and were not yet uploaded
            uploadOfflineTracks();
            // this delete the local tracks that were deleted from the cloud in another installation
            deleteCloudDeletedTracks();
        } else {
            Toast.makeText(this, getString(R.string.sync_service_no_net),
                    Toast.LENGTH_LONG).show();
            // TODO: refactor to snackbar
        }
    }

    private void setInstallationId(){
        trackRepository.setInstallationId();
    }

    private void downloadOnlineTracks() {
        MyLog.d(LOG_TAG, "downloadOnlineTracks");
        trackRepository.getAllTrackEntitiesFromCloud(
                new TrackRepository.GetAllTrackEntitiesFromCloudListener() {
                    @Override
                    public void onAllTracksLoaded(final List<TrackEntity> onlineTrackEntities) {
                        discIoExecutor.execute(new Runnable() {
                            @Override
                            public void run() {
                                saveDownloadedTracks(onlineTrackEntities);
                            }
                        });
                    }
                });
    }

    @WorkerThread
    private void saveDownloadedTracks(List<TrackEntity> onlineTrackEntities) {
        List<TrackEntity> offlineTrackEntities = trackRepository.getTracksSync();
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
    private void uploadOfflineTracks() {
        MyLog.d(LOG_TAG, "uploadOfflineTracks");

        List<TrackEntity> offlineTracks = trackRepository.getTracksSync();
        Iterator<TrackEntity> offlineTracksIterator = offlineTracks.iterator();

        // get all the tracks that has no firebase id,
        // which means that they are genuine offline local tracks
        while (offlineTracksIterator.hasNext()) {
            if (offlineTracksIterator.next().getFirebaseId() != null) {
                offlineTracksIterator.remove();
            }
        }

        for (TrackEntity trackEntity : offlineTracks) {
            trackRepository.saveTrackToCloudOnThread(trackEntity.getTrackId());
        }

        new DbSanitizer().sanitizeDb();
    }

    private void deleteCloudDeletedTracks(){
        MyLog.d(LOG_TAG, "deleteCloudDeletedTracks");


        // get the list of tracks that are present locally and has firebaseId, and are not present in cloud

        trackRepository.getCloudDeletedTracks(new TrackRepository.GetCloudDeletedTrackListener() {
            @Override
            public void onCloudDeletedTracksLoaded(List<String> deletedTracksFirebaseIds) {
                // delete the local instance of the tracks
                trackRepository.deleteCloudDeletedTracks(deletedTracksFirebaseIds);
            }
        });
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

