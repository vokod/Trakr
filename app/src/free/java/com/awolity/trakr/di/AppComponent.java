package com.awolity.trakr.di;

import com.awolity.trakr.location.LocationManager;
import com.awolity.trakr.repository.RoomTrackRepository;
import com.awolity.trakr.repository.SettingsRepository;
import com.awolity.trakr.repository.TrackRepository;
import com.awolity.trakr.trackrecorder.TrackRecorder;
import com.awolity.trakr.trackrecorder.TrackRecorderStatus;
import com.awolity.trakr.view.main.TrackRecorderServiceManager;
import com.awolity.trakr.viewmodel.SettingsViewModel;
import com.awolity.trakr.viewmodel.TrackListViewModel;
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

    void inject (TrackViewModel trackViewModel);

    void inject (TrackRecorderServiceManager serviceManager);

    void inject (TrackListViewModel trackListViewModel);

    void inject (TrakrWidget trakrWidget);

    void inject (SettingsRepository target);

    void inject (SettingsViewModel target);

    void inject (TrackRecorderStatus target);
}
