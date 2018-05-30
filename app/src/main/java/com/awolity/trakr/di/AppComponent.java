package com.awolity.trakr.di;

import com.awolity.trakr.location.LocationManager;
import com.awolity.trakr.repository.Repository;
import com.awolity.trakr.trackrecorder.TrakrNotification;
import com.awolity.trakr.trackrecorder.TrackRecorder;
import com.awolity.trakr.trackrecorder.TrackRecorderServiceManager;
import com.awolity.trakr.viewmodel.TrackListViewModel;
import com.awolity.trakr.viewmodel.TrackViewModel;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {AppModule.class, DbModule.class})
public interface AppComponent {

    void inject(LocationManager locationManager);

    void inject(TrackRecorder trackRecorder);

    void inject (Repository repository);

    void inject (TrackViewModel trackViewModel);

    void inject (TrackRecorderServiceManager serviceManager);

    void inject (TrakrNotification trakrNotification);

    void inject (TrackListViewModel trackListViewModel);

}
