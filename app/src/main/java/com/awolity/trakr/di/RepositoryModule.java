package com.awolity.trakr.di;

import com.awolity.trakr.repository.AppUserRepository;
import com.awolity.trakr.repository.remote.FirestoreTrackRepository;
import com.awolity.trakr.repository.local.RoomTrackRepository;
import com.awolity.trakr.repository.SettingsRepository;
import com.awolity.trakr.repository.TrackRepository;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@SuppressWarnings("WeakerAccess")
@Module
public class RepositoryModule {

    public RepositoryModule(){
    }

    @Provides
    @Singleton
    public RoomTrackRepository provideRoomTrackRepository() {
        return new RoomTrackRepository();
    }

    @Provides
    @Singleton
    public FirestoreTrackRepository provideFirebaseTrackRepository() {
        return new FirestoreTrackRepository();
    }

    @Provides
    @Singleton
    public TrackRepository provideTrackRepository() {
        return new TrackRepository();
    }

    @Provides
    @Singleton
    public AppUserRepository provideAppUserRepository() {
        return new AppUserRepository();
    }

    @Provides
    @Singleton
    public SettingsRepository provideSettingsRepository() {
        return new SettingsRepository();
    }
}
