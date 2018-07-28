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
import com.awolity.trakr.di.TrakrApplication;
import com.awolity.trakr.repository.TrackRepository;
import com.awolity.trakr.utils.MyLog;

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
        if (isConnected(this)) {
            downloadOnlineTracks();
        } else {
            Toast.makeText(this, getString(R.string.sync_service_no_net),
                    Toast.LENGTH_LONG).show();
            // TODO: refactor to snackbar
        }
    }

    private void downloadOnlineTracks() {
        // ezt úgy átalakítani, hogy először letöltse a trackeket, de ne rakja db-be
        // majd letöltse a trackpointokat, és ha ez megvan, akkor rakja be db-be a dolgokat
        MyLog.d(LOG_TAG, "downloadOnlineTracks");
        trackRepository.getAllTrackEntitiesFromCloud(
                new TrackRepository.GetAllTrackEntitiesFromCloudListener() {
            @Override
            public void onAllTracksLoaded(final List<TrackEntity> onlineTrackEntities) {
                discIoExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        saveDownloadedTracks(onlineTrackEntities);
                        uploadOfflineTracks();
                    }
                });
            }
        });
    }

    @WorkerThread
    private void saveDownloadedTracks(List<TrackEntity> onlineTrackEntities){
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

        // get all the tracks that has no firebase id
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

    private static boolean isConnected(Context context) {
        MyLog.d(LOG_TAG, "isConnected");
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }


}

