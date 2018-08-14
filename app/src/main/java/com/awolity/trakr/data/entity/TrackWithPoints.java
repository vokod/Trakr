package com.awolity.trakr.data.entity;

import android.arch.persistence.room.Relation;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.Exclude;

import java.util.ArrayList;
import java.util.List;

public class TrackWithPoints  extends TrackEntity{

    @Relation(parentColumn = "track_id", entityColumn = "track_id")
    private List<TrackpointEntity> trackPoints;

    public TrackWithPoints(){ }

    public List<TrackpointEntity> getTrackPoints() {
        return trackPoints;
    }

    public void setTrackPoints(List<TrackpointEntity> trackPoints) {
        this.trackPoints = trackPoints;
    }

    @Exclude
    public List<LatLng> getPointsLatLng(){
        List<LatLng> latLngs = new ArrayList<>(trackPoints.size());
        for(TrackpointEntity trackpointEntity : trackPoints){
            latLngs.add(new LatLng(trackpointEntity.getLatitude(),trackpointEntity.getLongitude()));
        }
        return latLngs;
    }

    @Exclude
    public void setTrackEntity(TrackEntity source){
        this.setTrackId(source.getTrackId());
        this.setFirebaseId(source.getFirebaseId());
        this.setTitle(source.getTitle());
        this.setStartTime(source.getStartTime());
        this.setDistance(source.getDistance());
        this.setAscent(source.getAscent());
        this.setDescent(source.getDescent());
        this.setElapsedTime(source.getElapsedTime());
        this.setNumOfTrackPoints(source.getNumOfTrackPoints());
        this.setNorthestPoint(source.getNorthestPoint());
        this.setSouthestPoint(source.getSouthestPoint());
        this.setWesternPoint(source.getWesternPoint());
        this.setEasternPoint(source.getEasternPoint());
        this.setMinAltitude(source.getMinAltitude());
        this.setMaxAltitude(source.getMaxAltitude());
        this.setMaxSpeed(source.getMaxSpeed());
        this.setAvgSpeed(source.getAvgSpeed());
        this.setMetadata(source.getMetadata());
    }

    @Exclude
    public TrackEntity getTrackEntity(){
        TrackEntity result = new TrackEntity();
        result.setTrackId(getTrackId());
        result.setFirebaseId(getFirebaseId());
        result.setTitle(getTitle());
        result.setStartTime(getStartTime());
        result.setDistance(getDistance());
        result.setAscent(getAscent());
        result.setDescent(getDescent());
        result.setElapsedTime(getElapsedTime());
        result.setNumOfTrackPoints(getNumOfTrackPoints());
        result.setNorthestPoint(getNorthestPoint());
        result.setSouthestPoint(getSouthestPoint());
        result.setWesternPoint(getWesternPoint());
        result.setEasternPoint(getEasternPoint());
        result.setMinAltitude(getMinAltitude());
        result.setMaxAltitude(getMaxAltitude());
        result.setMaxSpeed(getMaxSpeed());
        result.setAvgSpeed(getAvgSpeed());
        result.setMetadata(getMetadata());
        return result;
    }
}
