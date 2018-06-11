package com.awolity.trakr.sync;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.util.Pair;
import android.widget.Toast;

import com.awolity.trakr.data.entity.TrackEntity;
import com.awolity.trakr.di.TrakrApplication;
import com.awolity.trakr.repository.TrackRepository;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

public class SyncService extends IntentService {

    private static final String LOG_TAG = SyncService.class.getSimpleName();

    @Inject
    TrackRepository trackRepository;

    public SyncService() {
        super("SyncService");
        TrakrApplication.getInstance().getAppComponent().inject(this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        if (isConnected(this)) {
            final List<TrackEntity> tracksInDb = trackRepository.getTracksSync();

            trackRepository.getAllTrackEntitiesFromFirebase(new TrackRepository.GetAllTrackEntitiesFromFirebaseListener() {
                @Override
                public void onAllTracksLoaded(List<TrackEntity> trackEntityList) {
                    Pair<List<TrackEntity>, List<TrackEntity>> comparedTracks
                            = compareTracks(tracksInDb, trackEntityList);

                    uploadOfflineTracks(comparedTracks.first);
                    downloadOnlineTracks(comparedTracks.second);
                }
            });

        } else {
            Toast.makeText(this, "Can't synchronise tracks in offline state. Will try next time the app starts", Toast.LENGTH_LONG).show();
        }
    }

    private void uploadOfflineTracks(List<TrackEntity> offlineTracks) {
        for (TrackEntity trackEntity : offlineTracks) {
            trackRepository.saveTrackToFirebase(trackEntity.getTrackId());
        }
    }

    private void downloadOnlineTracks(List<TrackEntity> onlineTracks) {
        for (TrackEntity trackEntity : onlineTracks) {
            trackRepository.saveTrackToLocalDbFromFirebase(trackEntity.getFirebaseId());
        }
    }

    private static boolean isConnected(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }

    private static Pair<List<TrackEntity>, List<TrackEntity>> compareTracks(List<TrackEntity> tracksInDb, List<TrackEntity> tracksInCloud) {
        List<TrackEntity> onlyOfflineTracks = new ArrayList<>(tracksInDb);
        Iterator<TrackEntity> onlyOfflineIterator = onlyOfflineTracks.iterator();
        while (onlyOfflineIterator.hasNext()) {
            if (!onlyOfflineIterator.next().getFirebaseId().isEmpty()) {
                onlyOfflineIterator.remove();
            }
        }

        List<TrackEntity> onlyOnlineTracks = new ArrayList<>(tracksInCloud);
        for (TrackEntity offlineTrack : tracksInDb) {
            onlyOnlineTracks.remove(offlineTrack);
        }

        return new Pair<>(onlyOfflineTracks, onlyOnlineTracks);
    }

}

