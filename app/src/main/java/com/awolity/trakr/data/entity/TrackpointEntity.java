package com.awolity.trakr.data.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import android.location.Location;
import androidx.annotation.NonNull;

import com.google.firebase.firestore.PropertyName;

@SuppressWarnings("WeakerAccess")

@Entity(tableName = "trackpoint_table",
        foreignKeys =
        @ForeignKey(entity = TrackEntity.class,
                parentColumns = "track_id",
                childColumns = "track_id",
                onDelete = ForeignKey.CASCADE),
        indices = {@Index(value = "track_id")})
public class TrackpointEntity {

    @SuppressWarnings("NullableProblems")
    @NonNull
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "trackpoint_id")
    @PropertyName("id")
    long trackpointId;
    @PropertyName("tid")
    @ColumnInfo(name = "track_id")
    long trackId;
    @PropertyName("t")
    long time;
    @PropertyName("lat")
    double latitude;
    @PropertyName("lon")
    double longitude;
    @PropertyName("alt")
    double altitude;
    @PropertyName("ult")
    @ColumnInfo(name = "altitude_unfiltered")
    double unfilteredAltitude;
    @PropertyName("b")
    double bearing;
    @PropertyName("s")
    double speed;
    @ColumnInfo(name = "speed_unfiltered")
    @PropertyName("us")
    double unfilteredSpeed;
    @PropertyName("ac")
    double accuracy;
    @PropertyName("d")
    double distance;

    @NonNull
    @PropertyName("id")
    public long getTrackpointId() {
        return trackpointId;
    }

    @PropertyName("id")
    public void setTrackpointId(@NonNull long trackpointId) {
        this.trackpointId = trackpointId;
    }

    @PropertyName("tid")
    public long getTrackId() {
        return trackId;
    }

    @PropertyName("tid")
    public void setTrackId(long trackId) {
        this.trackId = trackId;
    }

    @PropertyName("t")
    public long getTime() {
        return time;
    }

    @PropertyName("t")
    public void setTime(long time) {
        this.time = time;
    }

    @PropertyName("lat")
    public double getLatitude() {
        return latitude;
    }

    @PropertyName("lat")
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    @PropertyName("lon")
    public double getLongitude() {
        return longitude;
    }

    @PropertyName("lon")
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @PropertyName("alt")
    public double getAltitude() {
        return altitude;
    }

    @PropertyName("alt")
    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    @PropertyName("ult")
    public double getUnfilteredAltitude() {
        return unfilteredAltitude;
    }

    @PropertyName("ult")
    public void setUnfilteredAltitude(double unfilteredAltitude) {
        this.unfilteredAltitude = unfilteredAltitude;
    }

    @PropertyName("b")
    public double getBearing() {
        return bearing;
    }

    @PropertyName("b")
    public void setBearing(double bearing) {
        this.bearing = bearing;
    }

    @PropertyName("s")
    public double getSpeed() {
        return speed;
    }

    @PropertyName("s")
    public void setSpeed(double speed) {
        this.speed = speed;
    }

    @PropertyName("us")
    public double getUnfilteredSpeed() {
        return unfilteredSpeed;
    }

    @PropertyName("us")
    public void setUnfilteredSpeed(double unfilteredSpeed) {
        this.unfilteredSpeed = unfilteredSpeed;
    }

    @PropertyName("ac")
    public double getAccuracy() {
        return accuracy;
    }

    @PropertyName("ac")
    public void setAccuracy(double accuracy) {
        this.accuracy = accuracy;
    }

    @PropertyName("d")
    public double getDistance() {
        return distance;
    }

    @PropertyName("d")
    public void setDistance(double distance) {
        this.distance = distance;
    }

    public static TrackpointEntity fromLocation(Location location) {
        TrackpointEntity tp = new TrackpointEntity();
        tp.setAltitude(location.getAltitude());
        tp.setAccuracy(location.getAccuracy());
        tp.setBearing(location.getBearing());
        tp.setLatitude(location.getLatitude());
        tp.setLongitude(location.getLongitude());
        tp.setUnfilteredSpeed(location.getSpeed() * 3.6);
        tp.setTime(location.getTime());
        tp.setUnfilteredAltitude(location.getAltitude());
        return tp;
    }

    @Override
    public String toString() {
        return "TrackpointEntity{" +
                "trackpointId=" + trackpointId +
                ", trackId=" + trackId +
                ", time=" + time +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", altitude=" + altitude +
                ", unfilteredAltitude=" + unfilteredAltitude +
                ", bearing=" + bearing +
                ", speed=" + speed +
                ", unfilteredSpeed=" + unfilteredSpeed +
                ", accuracy=" + accuracy +
                ", distance=" + distance +
                '}';
    }
}
