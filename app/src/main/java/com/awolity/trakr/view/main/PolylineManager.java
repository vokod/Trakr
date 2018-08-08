package com.awolity.trakr.view.main;

import android.content.Context;
import android.support.v4.content.ContextCompat;

import com.awolity.trakr.R;
import com.awolity.trakr.utils.MyLog;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.List;

public class PolylineManager {

    private static final String TAG = "PolylineManager";
    private PolylineOptions polylineOptions;
    private Polyline polyline;
    private Context context;

    PolylineManager(Context context) {
        this.context = context;
    }

    void drawPolyline(GoogleMap googleMap, List<LatLng> pointsCoordinates) {
        MyLog.d(TAG, "drawPolyline");
        if (googleMap != null) {
            clearPolyline(googleMap);
            setupPolyLine(googleMap);
            MyLog.d(TAG, "drawPolyline - setting points: " + pointsCoordinates.size());
            polyline.setPoints(pointsCoordinates);
        } else {
            MyLog.d(TAG, "drawPolyline - google maps is NULL");
        }
    }

    void clearPolyline(GoogleMap googleMap) {
        MyLog.d(TAG, "clearPolyline");
        googleMap.clear();
        polyline = null;
    }

    void continuePolyline(GoogleMap googleMap, LatLng currentLatLng) {
        MyLog.d(TAG, "continuePolyline");
        if (polylineOptions == null && googleMap != null) {
            setupPolyLine(googleMap);
        }
        List<LatLng> points = polyline.getPoints();
        points.add(currentLatLng);
        polyline.setPoints(points);
    }

    private void setupPolyLine(GoogleMap googleMap) {
        MyLog.d(TAG, "setupPolyLine");
        polylineOptions = new PolylineOptions()
                .geodesic(true)
                .color(ContextCompat.getColor(context, R.color.colorPrimary))
                .width(context.getResources().getInteger(R.integer.polyline_width))
                .zIndex(30)
                .visible(true);
        polyline = googleMap.addPolyline(polylineOptions);
    }
}

