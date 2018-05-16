package com.awolity.trakr.track;

import com.awolity.trakr.data.entity.TrackEntity;
import com.awolity.trakr.data.entity.TrackpointEntity;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.List;

public class TrackObject {

    private static final String TAG = TrackObject.class.getSimpleName();
    private TrackEntity trackEntity;

    private TrackObject() {
        trackEntity = new TrackEntity();
    }

    public TrackObject(String title) {
        this();
        trackEntity.setTitle(title);
        trackEntity.setStartTime(System.currentTimeMillis());
    }

    public TrackEntity getTrackEntity() {
        return trackEntity;
    }

    private void setTrackEntity(TrackEntity td) {
        this.trackEntity = td;
    }

    public void setMetadata(String metadata) {
        trackEntity.setMetadata(metadata);
    }

    public double getAvgSpeed() {
        return trackEntity.getAvgSpeed();
    }

    public double getMaxSpeed() {
        return trackEntity.getMaxSpeed();
    }


    public void addTrackPoint(TrackpointEntity tp) {
    // TODO
    }

    public String getTitle() {
        return trackEntity.getTitle();
    }

    public void setTitle(String mTitle) {
        trackEntity.setTitle(mTitle);
    }

    public long getStartTime() {
        return trackEntity.getStartTime();
    }

    public long getId() {
        return trackEntity.getTrackId();
    }

    public long getElapsedTime() {
        return trackEntity.getElapsedTime();
    }

    public double getDistance() {
        return trackEntity.getDistance();
    }

    public double getAscent() {
        return trackEntity.getAscent();
    }

    public double getDescent() {
        return trackEntity.getDescent();
    }

    private long getTrackPointsNum() {
        return trackEntity.getNumOfTrackPoints();
    }

    public TrackpointEntity getLastTrackPoint() {
      // TODO
        return null;
    }

    public List<LatLng> getTrackPoints() {
        return null;
    }

    public double getMaxAltitude() {
        return trackEntity.getMinAltitude();
    }

    public double geMinAltitude() {
        return trackEntity.getMaxAltitude();
    }

    public LatLngBounds getLatLngBounds() {
        return new LatLngBounds(
                new LatLng(trackEntity.getSouthestPoint(), trackEntity.getWesternPoint()),
                new LatLng(trackEntity.getNorthestPoint(), trackEntity.getEasternPoint()));
    }
}
