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
    private GoogleMap googleMap;

    public PolylineManager(Context context){
        this.context = context;
    }

    void setupPolyLine(GoogleMap googleMap) {
        MyLog.d(TAG, "setupPolyLine");
        this.googleMap = googleMap;
        polylineOptions = new PolylineOptions()
                .geodesic(true)
                .color(ContextCompat.getColor(context, R.color.colorPrimary))
                .width(context.getResources().getInteger(R.integer.polyline_width))
                .zIndex(30)
                .visible(true);

        if (googleMap != null) {
            // MyLog.d(TAG, "setupPolyLine - adding polyline to map");
            polyline = googleMap.addPolyline(polylineOptions);
        }
    }

    void drawPolyline(GoogleMap googleMap, List<LatLng> pointsCoordinates) {
        MyLog.d(TAG, "drawPolyline");
        setupPolyLine(googleMap);
        if (googleMap != null) {
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
        if (polylineOptions == null) {
            MyLog.d(TAG, "continuePolyline - polylineOptions is NULL, setting up");
            setupPolyLine(googleMap);
        } else {
            MyLog.d(TAG, "continuePolyline - polylineOptions is NOT NULL");
        }
        MyLog.d(TAG, "continuePolyline - getting polyline points");
        List<LatLng> points = polyline.getPoints();
        MyLog.d(TAG, "continuePolyline - num of points: " + points.size());
        MyLog.d(TAG, "continuePolyline - adding point to points");
        points.add(currentLatLng);
        MyLog.d(TAG, "continuePolyline - setting points to polyline");
        polyline.setPoints(points);
    }
}
