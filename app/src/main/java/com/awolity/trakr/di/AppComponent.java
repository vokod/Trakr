package com.awolity.trakr.di;

import com.awolity.trakr.location.LocationManager;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {AppModule.class, DbModule.class})
public interface AppComponent {

    void inject(LocationManager locationManager);

}
