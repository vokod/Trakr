package com.awolity.trakr.model;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TrackDataWithMapPoints extends TrackData {

    private List<MapPoint> mapPointList;

    public List<MapPoint> getMapPointList() {
        return mapPointList;
    }

    public void setMapPointList(List<MapPoint> mapPointList) {
        this.mapPointList = mapPointList;
    }

    public void setTrackData(TrackData trackData){
        this.setTrackId(trackData.getTrackId());
        this.setFirebaseId(trackData.getFirebaseId());
        this.setTitle(trackData.getTitle());
        this.setStartTime(trackData.getStartTime());
        this.setDistance(trackData.getDistance());
        this.setAscent(trackData.getAscent());
        this.setDescent(trackData.getDescent());
        this.setElapsedTime(trackData.getElapsedTime());
        this.setNumOfTrackPoints(trackData.getNumOfTrackPoints());
        this.setNorthestPoint(trackData.getNorthestPoint());
        this.setSouthestPoint(trackData.getSouthestPoint());
        this.setWesternPoint(trackData.getWesternPoint());
        this.setEasternPoint(trackData.getEasternPoint());
        this.setMinAltitude(trackData.getMinAltitude());
        this.setMaxAltitude(trackData.getMaxAltitude());
        this.setMaxSpeed(trackData.getMaxSpeed());
        this.setAvgSpeed(trackData.getAvgSpeed());
        this.setMetadata(trackData.getMetadata());
    }

    public TrackData getTrackData(){
        TrackData result = new TrackData();
        result.setTrackId(this.getTrackId());
        result.setFirebaseId(this.getFirebaseId());
        result.setTitle(this.getTitle());
        result.setStartTime(this.getStartTime());
        result.setDistance(this.getDistance());
        result.setAscent(this.getAscent());
        result.setDescent(this.getDescent());
        result.setElapsedTime(this.getElapsedTime());
        result.setNumOfTrackPoints(this.getNumOfTrackPoints());
        result.setNorthestPoint(this.getNorthestPoint());
        result.setSouthestPoint(this.getSouthestPoint());
        result.setWesternPoint(this.getWesternPoint());
        result.setEasternPoint(this.getEasternPoint());
        result.setMinAltitude(this.getMinAltitude());
        result.setMaxAltitude(this.getMaxAltitude());
        result.setMaxSpeed(this.getMaxSpeed());
        result.setAvgSpeed(this.getAvgSpeed());
        result.setMetadata(this.getMetadata());
        return result;
    }

    public List<LatLng> getMapPointsAsLatLngs(){
        LatLng[] latLngs = new LatLng[mapPointList.size()];
        for (int i = 0; i < latLngs.length; i++) {
            latLngs[i] = mapPointList.get(i).toLatLng();
        }
        return Arrays.asList(latLngs);
    }
}
