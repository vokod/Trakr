package com.awolity.trakr;

import android.app.Application;
import android.content.Intent;

import com.awolity.trakr.di.AppComponent;
import com.awolity.trakr.di.AppModule;
import com.awolity.trakr.di.DaggerAppComponent;
import com.awolity.trakr.di.DbModule;
import com.awolity.trakr.di.RepositoryModule;
import com.awolity.trakr.notification.NotificationUtils;
import com.awolity.trakr.sync.SyncService;
import com.awolity.trakr.trackrecorder.TrackRecorder;
import com.awolity.trakrutils.FileLoggingTree;
import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;
import timber.log.Timber;

public class TrakrApplication extends Application {

    private static final TrakrApplication instance = new TrakrApplication();
    private static AppComponent appComponent;

    public static TrakrApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        //FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        getAppComponent();

        NotificationUtils.setupNotificationChannels(this);

        // start syncing
        try {
            startService(new Intent(this, SyncService.class));
        } catch (IllegalStateException e) {
            Crashlytics.logException(e);
          // MyLog.e("TrakrApplication", e.getLocalizedMessage());
        }

        TrackRecorder.resetWidget(this);

        Timber.plant(new FileLoggingTree(getApplicationContext()));

      /* StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskReads()
                .detectDiskWrites()
                .detectNetwork()   // or .detectAll() for all detectable problems
                .penaltyLog()
                .build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects()
                .detectLeakedClosableObjects()
                .penaltyLog()
                .penaltyDeath()
                .build());*/
    }

    public AppComponent getAppComponent() {
        if (appComponent == null) {
            appComponent = DaggerAppComponent.builder()
                    .appModule(new AppModule(this))
                    .dbModule(new DbModule(this))
                    .repositoryModule(new RepositoryModule())
                    .build();
        }
        return appComponent;
    }
}
