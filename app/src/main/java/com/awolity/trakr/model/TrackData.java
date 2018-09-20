package com.awolity.trakr.model;

import com.awolity.trakrutils.Constants;

public class TrackData {

    private long trackId;
    private String firebaseId;
    private String title;
    private long startTime;
    private double distance;
    private double ascent;
    private double descent;
    private long elapsedTime;
    private long numOfTrackPoints;
    private double northestPoint;
    private double southestPoint;
    private double westernPoint;
    private double easternPoint;
    private double minAltitude;
    private double maxAltitude;
    private double maxSpeed;
    private double avgSpeed;
    private String metadata;

    public long getTrackId() {
        return trackId;
    }

    public void setTrackId(long trackId) {
        this.trackId = trackId;
    }

    public String getFirebaseId() {
        return firebaseId;
    }

    public void setFirebaseId(String firebaseId) {
        this.firebaseId = firebaseId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getAscent() {
        return ascent;
    }

    public void setAscent(double ascent) {
        this.ascent = ascent;
    }

    public double getDescent() {
        return descent;
    }

    public void setDescent(double descent) {
        this.descent = descent;
    }

    public long getElapsedTime() {
        return elapsedTime;
    }

    public void setElapsedTime(long elapsedTime) {
        this.elapsedTime = elapsedTime;
    }

    public long getNumOfTrackPoints() {
        return numOfTrackPoints;
    }

    public void setNumOfTrackPoints(long numOfTrackPoints) {
        this.numOfTrackPoints = numOfTrackPoints;
    }

    public double getNorthestPoint() {
        return northestPoint;
    }

    public void setNorthestPoint(double northestPoint) {
        this.northestPoint = northestPoint;
    }

    public double getSouthestPoint() {
        return southestPoint;
    }

    public void setSouthestPoint(double southestPoint) {
        this.southestPoint = southestPoint;
    }

    public double getWesternPoint() {
        return westernPoint;
    }

    public void setWesternPoint(double westernPoint) {
        this.westernPoint = westernPoint;
    }

    public double getEasternPoint() {
        return easternPoint;
    }

    public void setEasternPoint(double easternPoint) {
        this.easternPoint = easternPoint;
    }

    public double getMinAltitude() {
        return minAltitude;
    }

    public void setMinAltitude(double minAltitude) {
        this.minAltitude = minAltitude;
    }

    public double getMaxAltitude() {
        return maxAltitude;
    }

    public void setMaxAltitude(double maxAltitude) {
        this.maxAltitude = maxAltitude;
    }

    public double getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(double maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public double getAvgSpeed() {
        return avgSpeed;
    }

    public void setAvgSpeed(double avgSpeed) {
        this.avgSpeed = avgSpeed;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    public void convertToImperial() {
        setDistance(getDistance() / Constants.MILE);
        setAscent(getAscent() / Constants.FOOT);
        setDescent(getDescent() / Constants.FOOT);
        setMinAltitude(getMinAltitude() / Constants.FOOT);
        setMaxAltitude(getMaxAltitude() / Constants.FOOT);
        setMaxSpeed(getMaxSpeed() / Constants.MILE);
        setAvgSpeed(getAvgSpeed() / Constants.MILE);
    }
}
