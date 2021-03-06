package com.awolity.trakr.di;

import com.awolity.trakr.TrakrApplication;
import com.awolity.trakr.repository.local.model.TrakrDatabase;
import com.awolity.trakr.repository.local.model.dao.TrackDao;
import com.awolity.trakr.repository.local.model.dao.TrackpointDao;

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
