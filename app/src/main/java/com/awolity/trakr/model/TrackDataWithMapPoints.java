package com.awolity.trakr.model;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TrackDataWithMapPoints  {

    private List<MapPoint> mapPointList;
    private TrackData trackData;

    public List<MapPoint> getMapPointList() {
        return mapPointList;
    }

    public void setMapPointList(List<MapPoint> mapPointList) {
        this.mapPointList = mapPointList;
    }

    public void setTrackData(TrackData trackData){
      this.trackData = trackData;
    }

    public TrackData getTrackData(){
        return trackData;
    }

    public List<LatLng> getMapPointsAsLatLngs(){
        LatLng[] latLngs = new LatLng[mapPointList.size()];
        for (int i = 0; i < latLngs.length; i++) {
            latLngs[i] = mapPointList.get(i).toLatLng();
        }
        return Arrays.asList(latLngs);
    }
}
