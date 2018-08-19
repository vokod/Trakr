package com.awolity.trakr.view;

import android.content.Context;
import android.support.v4.content.ContextCompat;

import com.awolity.trakr.data.entity.TrackEntity;
import com.awolity.trakr.data.entity.TrackWithPoints;
import com.awolity.trakr.viewmodel.model.MapPoint;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

public class MapUtils {

    private static final String TAG = "MapUtils";

    private MapUtils() {
    }

    public static Polyline setupTrackPolyLine(Context context, GoogleMap googleMap,
                                              List<MapPoint> mapPoints) {
      // MyLog.d(TAG, "setupTrackPolyLine");
        Polyline polyline = null;
        PolylineOptions polylineOptions = new PolylineOptions()
                .geodesic(true)
                .color(ContextCompat.getColor(context, com.awolity.trakrutils.R.color.colorPrimary))
                .width(context.getResources().getInteger(com.awolity.trakrutils.R.integer.polyline_width))
                .zIndex(30)
                .visible(true);

        List<LatLng> latLngs = new ArrayList<>(mapPoints.size());
        for(MapPoint mapPoint : mapPoints){
            latLngs.add(mapPoint.toLatLng());
        }
        return googleMap.addPolyline(polylineOptions.addAll(latLngs));
    }

    public static void moveCameraToTrack(GoogleMap googleMap, TrackEntity trackEntity) {
      // MyLog.d(TAG, "moveCameraToTrack");
        LatLngBounds bounds = new LatLngBounds(
                new LatLng(trackEntity.getSouthestPoint(), trackEntity.getWesternPoint()),
                new LatLng(trackEntity.getNorthestPoint(), trackEntity.getEasternPoint()));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
    }

    public static Polyline setupTrackPolyLine(Context context, GoogleMap googleMap, TrackWithPoints trackWithPoints, boolean moveCamera) {
        // MyLog.d(LOG_TAG, "setupTrackPolyLine");
        Polyline polyline = null;
        PolylineOptions polylineOptions = new PolylineOptions()
                .geodesic(true)
                .color(ContextCompat.getColor(context, com.awolity.trakrutils.R.color.colorPrimary))
                .width(context.getResources().getInteger(com.awolity.trakrutils.R.integer.polyline_width))
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
