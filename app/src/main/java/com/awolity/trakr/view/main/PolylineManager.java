package com.awolity.trakr.view.main;

import android.content.Context;
import android.support.v4.content.ContextCompat;

import com.awolity.trakr.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.List;

@SuppressWarnings("WeakerAccess")
public class PolylineManager {

    private PolylineOptions polylineOptions;
    private Polyline polyline;
    private final Context context;

    PolylineManager(Context context) {
        this.context = context;
    }

    void drawPolyline(GoogleMap googleMap, List<LatLng> pointsCoordinates) {
        if (googleMap != null) {
            clearPolyline(googleMap);
            setupPolyLine(googleMap);
            polyline.setPoints(pointsCoordinates);
        }
    }

    void clearPolyline(GoogleMap googleMap) {
        googleMap.clear();
        polyline = null;
    }

    void continuePolyline(GoogleMap googleMap, LatLng currentLatLng) {
        if (polylineOptions == null && googleMap != null) {
            setupPolyLine(googleMap);
        }
        List<LatLng> points = polyline.getPoints();
        points.add(currentLatLng);
        polyline.setPoints(points);
    }

    private void setupPolyLine(GoogleMap googleMap) {
        polylineOptions = new PolylineOptions()
                .geodesic(true)
                .color(ContextCompat.getColor(context, R.color.colorPrimary))
                .width(context.getResources().getInteger(R.integer.polyline_width))
                .zIndex(30)
                .visible(true);
        polyline = googleMap.addPolyline(polylineOptions);
    }
}

