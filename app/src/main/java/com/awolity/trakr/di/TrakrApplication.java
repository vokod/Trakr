package com.awolity.trakr.di;

import android.app.Application;
import android.content.Intent;
import android.os.StrictMode;

import com.awolity.trakr.notification.NotificationUtils;
import com.awolity.trakr.sync.SyncService;
import com.awolity.trakr.utils.MyLog;
import com.google.firebase.database.FirebaseDatabase;

public class TrakrApplication extends Application {

    private static final TrakrApplication instance = new TrakrApplication();
    private static AppComponent appComponent;

    public static TrakrApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // Fabric.with(this, new Crashlytics());
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        getAppComponent();
        NotificationUtils.setupNotificationChannels(this);
        try {
            startService(new Intent(this, SyncService.class));
        } catch (IllegalStateException e){
            MyLog.e("TrakrApplication", e.getLocalizedMessage() );
        }


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
                    .build();
        }
        return appComponent;
    }
}
