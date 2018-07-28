package com.awolity.trakr.di;

import com.awolity.trakr.location.LocationManager;
import com.awolity.trakr.repository.FirebaseTrackRepository;
import com.awolity.trakr.repository.RoomTrackRepository;
import com.awolity.trakr.repository.TrackRepository;
import com.awolity.trakr.sync.DbSanitizer;
import com.awolity.trakr.sync.SyncService;
import com.awolity.trakr.trackrecorder.TrackRecorder;
import com.awolity.trakr.trackrecorder.TrackRecorderServiceManager;
import com.awolity.trakr.viewmodel.TrackListViewModel;
import com.awolity.trakr.viewmodel.TrackViewModel;
import com.awolity.trakr.widget.TrakrWidget;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {AppModule.class, DbModule.class, RepositoryModule.class})
public interface AppComponent {

    void inject(LocationManager locationManager);

    void inject(TrackRecorder trackRecorder);

    void inject (TrackRepository trackRepository);

    void inject (RoomTrackRepository roomTrackRepository);

    void inject (FirebaseTrackRepository firebaseTrackRepository);

    void inject (TrackViewModel trackViewModel);

    void inject (TrackRecorderServiceManager serviceManager);

    void inject (TrackListViewModel trackListViewModel);

    void inject (SyncService syncService);

    void inject (TrakrWidget trakrWidget);

    void inject (DbSanitizer dbSanitizer);

}
