package com.awolity.trakr.model;

import com.awolity.trakr.data.entity.TrackEntity;

public class TrackDataTrackEntityConverter {

    private TrackDataTrackEntityConverter(){}

    public static TrackData toTrackData(TrackEntity trackEntity){
        TrackData result = new TrackData();
        result.setTrackId(trackEntity.getTrackId());
        result.setFirebaseId(trackEntity.getFirebaseId());
        result.setTitle(trackEntity.getTitle());
        result.setStartTime(trackEntity.getStartTime());
        result.setDistance(trackEntity.getDistance());
        result.setAscent(trackEntity.getAscent());
        result.setDescent(trackEntity.getDescent());
        result.setElapsedTime(trackEntity.getElapsedTime());
        result.setNumOfTrackPoints(trackEntity.getNumOfTrackPoints());
        result.setNorthestPoint(trackEntity.getNorthestPoint());
        result.setSouthestPoint(trackEntity.getSouthestPoint());
        result.setWesternPoint(trackEntity.getWesternPoint());
        result.setEasternPoint(trackEntity.getEasternPoint());
        result.setMinAltitude(trackEntity.getMinAltitude());
        result.setMaxAltitude(trackEntity.getMaxAltitude());
        result.setMaxSpeed(trackEntity.getMaxSpeed());
        result.setAvgSpeed(trackEntity.getAvgSpeed());
        result.setMetadata(trackEntity.getMetadata());
        return result;
    }
}
