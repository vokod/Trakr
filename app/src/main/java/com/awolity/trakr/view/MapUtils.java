package com.awolity.trakr.view;

import android.content.Context;
import android.support.v4.content.ContextCompat;

import com.awolity.trakr.R;
import com.awolity.trakr.data.entity.TrackEntity;
import com.awolity.trakr.data.entity.TrackWithPoints;
import com.awolity.trakr.utils.MyLog;
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
                .color(ContextCompat.getColor(context, R.color.colorPrimary))
                .width(context.getResources().getInteger(R.integer.polyline_width))
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


}
