package com.awolity.trakr.view.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.awolity.trakr.R;
import com.awolity.trakr.TrakrApplication;
import com.awolity.trakr.repository.TrackRepository;
import com.awolity.trakr.view.main.MainActivity;
import com.awolity.trakr.utils.Constants;

import javax.inject.Inject;

public class TrakrWidget extends AppWidgetProvider {

    public static final String TAG = TrakrWidget.class.getSimpleName();

    @Inject
    TrackRepository repository;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        TrakrApplication.getInstance().getAppComponent().inject(this);
        // MyLog.d(TAG, "onUpdate");
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    public static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                       int appWidgetId) {
        // MyLog.d(TAG, "updateAppWidget");

        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent showPendingIntent = PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_blank);
        // Widgets allow click handlers to only launch pending intents
        views.setOnClickPendingIntent(R.id.ll_widget, showPendingIntent);

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    public static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                       int appWidgetId, String duration, String distance, int unit) {
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent showPendingIntent = PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);

        // Update image and text
        views.setTextViewText(R.id.tv_value_duration, duration);
        views.setTextViewText(R.id.tv_unit_duration, context.getString(R.string.elapsed_time_view_unit));
        views.setTextViewText(R.id.tv_label_duration, context.getString(R.string.elapsed_time_view_title));
        views.setImageViewResource(R.id.iv_icon_distance, R.drawable.ic_distance);
        views.setImageViewResource(R.id.iv_icon, R.drawable.ic_duration);
        if(unit == Constants.UNIT_IMPERIAL){
            views.setTextViewText(R.id.tv_value_distance, distance);
            views.setTextViewText(R.id.tv_unit_distance, context.getString(R.string.distance_view_unit_imperial));
            views.setTextViewText(R.id.tv_label_distance, context.getString(R.string.distance_view_title));
        } else {
            views.setTextViewText(R.id.tv_value_distance, distance);
            views.setTextViewText(R.id.tv_unit_distance, context.getString(R.string.distance_view_unit));
            views.setTextViewText(R.id.tv_label_distance, context.getString(R.string.distance_view_title));
        }

        // Widgets allow click handlers to only launch pending intents
        views.setOnClickPendingIntent(R.id.ll_widget, showPendingIntent);

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onEnabled(Context context) {
    }

    @Override
    public void onDisabled(Context context) {
    }
}
