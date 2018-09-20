package com.awolity.trakr.di;

import com.awolity.trakr.location.LocationManager;
import com.awolity.trakr.repository.AppUserRepository;
import com.awolity.trakr.repository.FirebaseTrackRepository;
import com.awolity.trakr.repository.RoomTrackRepository;
import com.awolity.trakr.repository.SettingsRepository;
import com.awolity.trakr.repository.TrackRepository;
import com.awolity.trakr.sync.DbSanitizer;
import com.awolity.trakr.sync.SyncService;
import com.awolity.trakr.trackrecorder.TrackRecorder;
import com.awolity.trakr.trackrecorder.TrackRecorderStatus;
import com.awolity.trakr.view.detail.TrackDetailViewModel;
import com.awolity.trakr.view.explore.ExploreViewModel;
import com.awolity.trakr.view.main.TrackRecorderServiceManager;
import com.awolity.trakr.viewmodel.AppUserViewModel;
import com.awolity.trakr.viewmodel.SettingsViewModel;
import com.awolity.trakr.view.list.TrackListViewModel;
import com.awolity.trakr.viewmodel.TrackViewModel;
import com.awolity.trakr.view.widget.TrakrWidget;

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

    void inject (TrackDetailViewModel target);

    void inject (AppUserViewModel target);

    void inject (TrackRecorderServiceManager serviceManager);

    void inject (TrackListViewModel trackListViewModel);

    void inject (ExploreViewModel target);

    void inject (SyncService syncService);

    void inject (TrakrWidget trakrWidget);

    void inject (DbSanitizer dbSanitizer);

    void inject (SettingsRepository target);

    void inject (SettingsViewModel target);

    void inject (TrackRecorderStatus target);

    void inject (AppUserRepository target);

}
