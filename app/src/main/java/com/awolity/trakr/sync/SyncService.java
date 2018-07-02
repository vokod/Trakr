package com.awolity.trakr.sync;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
            // uploadOfflineTracks();
        } else {
            Toast.makeText(this, getString(R.string.sync_service_no_net), Toast.LENGTH_LONG).show();
        }
    }

    private void uploadOfflineTracks() {
        MyLog.d(LOG_TAG, "uploadOfflineTracks");
        List<TrackEntity> offlineTracks = trackRepository.getTracksSync();
        Iterator<TrackEntity> offlineTracksIterator = offlineTracks.iterator();

        while (offlineTracksIterator.hasNext()) {
            if (offlineTracksIterator.next().getFirebaseId() != null) {
                offlineTracksIterator.remove();
            }
        }

        for (TrackEntity trackEntity : offlineTracks) {
            trackRepository.saveTrackToCloud(trackEntity.getTrackId());
        }
        // sanitize db
        new DbSanitizer().sanitizeDb();
    }

    private void downloadOnlineTracks() {
        MyLog.d(LOG_TAG, "downloadOnlineTracks");
        trackRepository.getAllTrackEntitiesFromCloud(new TrackRepository.GetAllTrackEntitiesFromFirebaseListener() {
            @Override
            public void onAllTracksLoaded(final List<TrackEntity> onlineTrackEntities) {
                discIoExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        List<TrackEntity> offlineTrackEntities = trackRepository.getTracksSync();
                        for (TrackEntity offlineTrack : offlineTrackEntities) {
                            for (TrackEntity onlineTrack : onlineTrackEntities) {
                                if (onlineTrack.getStartTime() == offlineTrack.getStartTime()) {
                                    onlineTrackEntities.remove(onlineTrack);
                                    break;
                                }
                            }
                        }
                        for (TrackEntity onlineTrack : onlineTrackEntities) {
                            trackRepository.saveTrackToLocalDbFromCloud(onlineTrack.getFirebaseId());
                        }
                        uploadOfflineTracks();
                    }
                });
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

