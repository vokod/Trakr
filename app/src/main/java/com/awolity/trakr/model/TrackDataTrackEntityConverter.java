package com.awolity.trakr.model;

import com.awolity.trakr.data.entity.TrackEntity;

public class TrackDataTrackEntityConverter {

    private TrackDataTrackEntityConverter(){}

    public static TrackEntity toTrackEntity(TrackData trackData){
        TrackEntity result = new TrackEntity();
        result.setTrackId(trackData.getTrackId());
        result.setFirebaseId(trackData.getFirebaseId());
        result.setTitle(trackData.getTitle());
        result.setStartTime(trackData.getStartTime());
        result.setDistance(trackData.getDistance());
        result.setAscent(trackData.getAscent());
        result.setElapsedTime(trackData.getElapsedTime());
        result.setNumOfTrackPoints(trackData.getNumOfTrackPoints());
        result.setNorthestPoint(trackData.getNorthestPoint());
        result.setSouthestPoint(trackData.getSouthestPoint());
        result.setWesternPoint(trackData.getWesternPoint());
        result.setEasternPoint(trackData.getEasternPoint());
        result.setMinAltitude(trackData.getMinAltitude());
        result.setMaxAltitude(trackData.getMaxAltitude());
        result.setMaxSpeed(trackData.getMaxSpeed());
        result.setAvgSpeed(trackData.getAvgSpeed());
        result.setMetadata(trackData.getMetadata());
        return result;
    }

    public static TrackData toTrackData(TrackEntity trackEntity){
        TrackData result = new TrackData();
        result.setTrackId(trackEntity.getTrackId());
        result.setFirebaseId(trackEntity.getFirebaseId());
        result.setTitle(trackEntity.getTitle());
        result.setStartTime(trackEntity.getStartTime());
        result.setDistance(trackEntity.getDistance());
        result.setAscent(trackEntity.getAscent());
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
