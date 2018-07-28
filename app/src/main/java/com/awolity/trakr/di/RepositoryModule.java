package com.awolity.trakr.di;

import com.awolity.trakr.repository.FirebaseTrackRepository;
import com.awolity.trakr.repository.RoomTrackRepository;
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
    public FirebaseTrackRepository provideFirebaseTrackRepository() {
        return new FirebaseTrackRepository();
    }

    @Provides
    @Singleton
    public TrackRepository provideTrackRepository() {
        return new TrackRepository();
    }
}
