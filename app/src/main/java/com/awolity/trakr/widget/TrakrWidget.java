package com.awolity.trakr.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.awolity.trakr.R;
import com.awolity.trakr.di.TrakrApplication;
import com.awolity.trakr.repository.TrackRepository;
import com.awolity.trakr.utils.MyLog;
import com.awolity.trakr.view.main.MainActivity;

import javax.inject.Inject;

public class TrakrWidget extends AppWidgetProvider {

    public static final String LOG_TAG = TrakrWidget.class.getSimpleName();

    @Inject
    TrackRepository repository;

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        TrakrApplication.getInstance().getAppComponent().inject(this);

        MyLog.d(LOG_TAG,"onReceive");
        super.onReceive(context, intent);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        TrakrApplication.getInstance().getAppComponent().inject(this);

        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    private static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                        int appWidgetId) {

        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent showPendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);

        // Update image and text
        views.setTextViewText(R.id.tv_value_duration, "");
        views.setTextViewText(R.id.tv_value_distance, "");
        views.setImageViewResource(R.id.iv_initial, 0);

        // Widgets allow click handlers to only launch pending intents
        views.setOnClickPendingIntent(R.id.cl_item, showPendingIntent);

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onEnabled(Context context) {
    }

    @Override
    public void onDisabled(Context context) {
    }
}
