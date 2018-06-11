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
}
