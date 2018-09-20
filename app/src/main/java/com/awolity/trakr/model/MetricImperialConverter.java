package com.awolity.trakr.model;

import com.awolity.trakrutils.Constants;

public class MetricImperialConverter {

    private MetricImperialConverter() {
    }

    public static TrackData toImperial(TrackData source) {
        TrackData result = new TrackData();
        result.setTrackId(source.getTrackId());
        result.setFirebaseId(source.getFirebaseId());
        result.setTitle(source.getTitle());
        result.setStartTime(source.getStartTime());
        result.setDistance(source.getDistance() / Constants.MILE);
        result.setAscent(source.getAscent() / Constants.FOOT);
        result.setDescent(source.getDescent() / Constants.FOOT);
        result.setElapsedTime(source.getElapsedTime());
        result.setNumOfTrackPoints(source.getNumOfTrackPoints());
        result.setNorthestPoint(source.getNorthestPoint());
        result.setSouthestPoint(source.getSouthestPoint());
        result.setWesternPoint(source.getWesternPoint());
        result.setEasternPoint(source.getEasternPoint());
        result.setMinAltitude(source.getMinAltitude() / Constants.FOOT);
        result.setMaxAltitude(source.getMaxAltitude() / Constants.FOOT);
        result.setMaxSpeed(source.getMaxSpeed() / Constants.MILE);
        result.setAvgSpeed(source.getAvgSpeed() / Constants.MILE);
        result.setMetadata(source.getMetadata());
        return result;
    }
}
