package com.awolity.trakr.view;

import android.content.Context;
import android.support.v4.content.ContextCompat;

import com.awolity.trakr.R;
import com.awolity.trakr.data.entity.TrackWithPoints;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

public class MapUtils {

    private MapUtils() {
    }

    public static Polyline setupTrackPolyLine(Context context, GoogleMap googleMap, TrackWithPoints trackWithPoints, boolean moveCamera) {
        // MyLog.d(LOG_TAG, "setupTrackPolyLine");
        Polyline polyline = null;
        PolylineOptions polylineOptions = new PolylineOptions()
                .geodesic(true)
                .color(ContextCompat.getColor(context, R.color.colorPrimary))
                .width(context.getResources().getInteger(R.integer.polyline_width))
                .zIndex(30)
                .visible(true);


        polyline = googleMap.addPolyline(polylineOptions.addAll(trackWithPoints.getPointsLatLng()));
        if (moveCamera) {
            moveCameraToTrack(googleMap, trackWithPoints);
        }

        return polyline;
    }

    public static void moveCameraToTrack(GoogleMap googleMap, TrackWithPoints trackWithPoints) {
        // MyLog.d(LOG_TAG, "moveCameraToTrack");
        LatLngBounds bounds = new LatLngBounds(
                new LatLng(trackWithPoints.getSouthestPoint(), trackWithPoints.getWesternPoint()),
                new LatLng(trackWithPoints.getNorthestPoint(), trackWithPoints.getEasternPoint()));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
    }


}
