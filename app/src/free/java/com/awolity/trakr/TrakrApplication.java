package com.awolity.trakr;

import android.app.Application;
import android.content.Intent;
import android.os.StrictMode;

import com.awolity.trakr.di.AppComponent;
import com.awolity.trakr.di.AppModule;
import com.awolity.trakr.di.DaggerAppComponent;
import com.awolity.trakr.di.DbModule;
import com.awolity.trakr.di.RepositoryModule;
import com.awolity.trakr.notification.NotificationUtils;
import com.awolity.trakr.trackrecorder.TrackRecorder;
import com.awolity.trakrutils.FileLoggingTree;
import com.awolity.trakrutils.MyLog;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.ads.MobileAds;

import io.fabric.sdk.android.Fabric;
import timber.log.Timber;

public class TrakrApplication extends Application {

    private static final TrakrApplication instance = new TrakrApplication();
    private static AppComponent appComponent;
    private static final String TAG = "TrakrApplication";

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

        MobileAds.initialize(this, getString(R.string.admob_app_id));

        TrackRecorder.resetWidget(this);

        // Timber.plant(new FileLoggingTree(getApplicationContext()));

      /*  StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskReads()
                .detectDiskWrites()
                .detectAll()
                // .detectNetwork()   // or .detectAll() for all detectable problems
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