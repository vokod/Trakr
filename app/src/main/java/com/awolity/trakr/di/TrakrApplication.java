package com.awolity.trakr.di;

import android.app.Application;
import android.os.StrictMode;
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;

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
        getAppComponent();

        if (false) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
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
                    .build());
        }
    }

    public AppComponent getAppComponent() {
        if (appComponent == null) {
            appComponent = DaggerAppComponent.builder()
                    .appModule(new AppModule(this))
                    .dbModule (new DbModule(this))
                    .build();
        }
        return appComponent;
    }
}
