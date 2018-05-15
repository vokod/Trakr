package com.awolity.trakr.di;

import com.awolity.trakr.data.TrakrDatabase;
import com.awolity.trakr.data.dao.TrackDao;
import com.awolity.trakr.data.dao.TrackpointDao;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@SuppressWarnings("WeakerAccess")
@Module
public class DbModule {

    private final TrakrApplication app;

    public DbModule(TrakrApplication app) {
        this.app = app;
    }

    @Provides
    @Singleton
    public TrakrDatabase provideSpotDatabase() {
        return TrakrDatabase.getDatabase(app);
    }

    @Provides
    @Singleton
    public TrackDao provideUserDao() {
        return provideSpotDatabase().trackDao();
    }

    @Provides
    @Singleton
    public TrackpointDao provideSpotDao() {
        return provideSpotDatabase().trackPointDao();
    }
}
