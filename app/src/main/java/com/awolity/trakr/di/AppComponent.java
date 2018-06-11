package com.awolity.trakr.di;

import com.awolity.trakr.location.LocationManager;
import com.awolity.trakr.repository.TrackRepository;
import com.awolity.trakr.sync.SyncService;
import com.awolity.trakr.trackrecorder.TrackRecorder;
import com.awolity.trakr.trackrecorder.TrackRecorderServiceManager;
import com.awolity.trakr.viewmodel.AppUserViewModel;
import com.awolity.trakr.viewmodel.TrackListViewModel;
import com.awolity.trakr.viewmodel.TrackViewModel;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {AppModule.class, DbModule.class})
public interface AppComponent {

    void inject(LocationManager locationManager);

    void inject(TrackRecorder trackRecorder);

    void inject (TrackRepository trackRepository);

    void inject (TrackViewModel trackViewModel);

    void inject (TrackRecorderServiceManager serviceManager);

    void inject (TrackListViewModel trackListViewModel);

    void inject (SyncService syncService);

}
