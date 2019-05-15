package com.awolity.trakr.data.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import android.content.Context;
import androidx.annotation.NonNull;

import com.awolity.trakr.R;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.PropertyName;

import java.util.Calendar;
import java.util.Objects;

@SuppressWarnings("WeakerAccess")
@Entity(tableName = "track_table")
public class TrackEntity {
    @Ignore
    private static final String TAG = TrackEntity.class.getSimpleName();

    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "track_id")
    @PropertyName("id")
    long trackId;
    @ColumnInfo(name = "firabase_id")
    @PropertyName("fid")
    String firebaseId;
    @PropertyName("tit")
    String title;
    @ColumnInfo(name = "start_time")
    @PropertyName("st")
    long startTime;
    @PropertyName("d")
    double distance;
    @PropertyName("asc")
    double ascent;
    @PropertyName("dsc")
    double descent;
    @ColumnInfo(name = "elapsed_time")
    @PropertyName("et")
    long elapsedTime;
    @ColumnInfo(name = "num_of_trackpoints")
    @PropertyName("pno")
    long numOfTrackPoints;
    @ColumnInfo(name = "northest_point")
    @PropertyName("np")
    double northestPoint;
    @ColumnInfo(name = "southest_point")
    @PropertyName("sp")
    double southestPoint;
    @ColumnInfo(name = "western_point")
    @PropertyName("wp")
    double westernPoint;
    @ColumnInfo(name = "eastern_point")
    @PropertyName("ep")
    double easternPoint;
    @ColumnInfo(name = "lowest_point")
    @PropertyName("lp")
    double minAltitude;
    @ColumnInfo(name = "highest_point")
    @PropertyName("hp")
    double maxAltitude;
    @ColumnInfo(name = "max_speed")
    @PropertyName("smx")
    double maxSpeed;
    @ColumnInfo(name = "avg_speed")
    @PropertyName("sa")
    double avgSpeed;
    @PropertyName("mtd")
    String metadata;
    @Ignore
    @Exclude
    private boolean isValidElevationData = false;

    @NonNull
    @PropertyName("id")
    public long getTrackId() {
        return trackId;
    }

    @PropertyName("id")
    public void setTrackId(@NonNull long trackId) {
        this.trackId = trackId;
    }

    @PropertyName("fid")
    public String getFirebaseId() {
        return firebaseId;
    }

    @PropertyName("fid")
    public void setFirebaseId(String firebaseId) {
        this.firebaseId = firebaseId;
    }

    @PropertyName("tit")
    public String getTitle() {
        return title;
    }

    @PropertyName("tit")
    public void setTitle(String title) {
        this.title = title;
    }

    @PropertyName("st")
    public long getStartTime() {
        return startTime;
    }

    @PropertyName("st")
    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    @PropertyName("d")
    public double getDistance() {
        return distance;
    }

    @PropertyName("d")
    public void setDistance(double distance) {
        this.distance = distance;
    }

    @PropertyName("asc")
    public double getAscent() {
        return ascent;
    }

    @PropertyName("asc")
    public void setAscent(double ascent) {
        this.ascent = ascent;
    }

    @PropertyName("dsc")
    public double getDescent() {
        return descent;
    }

    @PropertyName("dsc")
    public void setDescent(double descent) {
        this.descent = descent;
    }

    @PropertyName("et")
    public long getElapsedTime() {
        return elapsedTime;
    }

    @PropertyName("et")
    public void setElapsedTime(long elapsedTime) {
        this.elapsedTime = elapsedTime;
    }

    @PropertyName("pno")
    public long getNumOfTrackPoints() {
        return numOfTrackPoints;
    }

    @PropertyName("pno")
    public void setNumOfTrackPoints(long numOfTrackPoints) {
        this.numOfTrackPoints = numOfTrackPoints;
    }

    @PropertyName("np")
    public double getNorthestPoint() {
        return northestPoint;
    }

    @PropertyName("np")
    public void setNorthestPoint(double northestPoint) {
        this.northestPoint = northestPoint;
    }

    @PropertyName("sp")
    public double getSouthestPoint() {
        return southestPoint;
    }

    @PropertyName("sp")
    public void setSouthestPoint(double southestPoint) {
        this.southestPoint = southestPoint;
    }

    @PropertyName("wp")
    public double getWesternPoint() {
        return westernPoint;
    }

    @PropertyName("wp")
    public void setWesternPoint(double westernPoint) {
        this.westernPoint = westernPoint;
    }

    @PropertyName("ep")
    public double getEasternPoint() {
        return easternPoint;
    }

    @PropertyName("ep")
    public void setEasternPoint(double easternPoint) {
        this.easternPoint = easternPoint;
    }

    @PropertyName("lp")
    public double getMaxAltitude() {
        return maxAltitude;
    }

    @PropertyName("lp")
    public void setMinAltitude(double minAltitude) {
        this.minAltitude = minAltitude;
    }

    @PropertyName("hp")
    public double getMinAltitude() {
        return minAltitude;
    }

    @PropertyName("hp")
    public void setMaxAltitude(double maxAltitude) {
        this.maxAltitude = maxAltitude;
    }

    @PropertyName("smx")
    public double getMaxSpeed() {
        return maxSpeed;
    }

    @PropertyName("smx")
    public void setMaxSpeed(double maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    @PropertyName("sa")
    public double getAvgSpeed() {
        return avgSpeed;
    }

    @PropertyName("sa")
    public void setAvgSpeed(double avgSpeed) {
        this.avgSpeed = avgSpeed;
        if (maxSpeed == 0) {
            setMaxSpeed(avgSpeed);
        }
    }

    @PropertyName("mtd")
    public String getMetadata() {
        return metadata;
    }

    @PropertyName("mtd")
    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    public void increaseDistance(double delta) {
        distance += delta;
    }

    public void increaseAscent(double delta) {
        ascent += delta;
    }

    public void increaseDescent(double delta) {
        descent += delta;
    }

    public void increaseElapsedTime(long newTime) {
        elapsedTime = newTime - startTime;
    }

    public void increaseNumOfTrackpoints() {
        numOfTrackPoints++;
    }

    public void calculateAvgSpeed() {
        setAvgSpeed(getDistance() / ((double) getElapsedTime() / 1000) * 3.6);
    }

    public void calculateAscentDescent(TrackpointEntity actualTp, TrackpointEntity previousTp) {
        if (previousTp != null) {
            if (actualTp.getAltitude() > previousTp.getAltitude()) {
                // felfelé megyünk
                increaseAscent(actualTp.getAltitude() - previousTp.getAltitude());
            } else {
                // lefelé megyünk, vagy egyenlő a magasságunk
                increaseDescent(previousTp.getAltitude() - actualTp.getAltitude());
            }
        }
    }

    public void checkSetExtremeValues(TrackpointEntity tp) {
        checkSetNorth(tp.getLatitude());
        checkSetSouth(tp.getLatitude());
        checkSetWest(tp.getLongitude());
        checkSetEast(tp.getLongitude());
        checkSetHighLow(tp.getAltitude());
        checkSetMaxSpeed(tp.getSpeed());
    }

    public void checkSetHighLow(double altitude) {
        if (!isValidElevationData && altitude != 0) {
            // there was no valid elevation data before, only 0-s
            maxAltitude = altitude;
            minAltitude = altitude;
            isValidElevationData = true;
        } else {
            // we already had valid elevation data
            if (altitude > maxAltitude) {
                maxAltitude = altitude;
            }
            if (altitude < minAltitude) {
                minAltitude = altitude;
            }
        }
    }

    private void checkSetNorth(double north) {
        if (north > northestPoint || northestPoint == 0) {
            northestPoint = north;
        }
    }

    private void checkSetSouth(double south) {
        if (southestPoint > south || southestPoint == 0) {
            southestPoint = south;
        }
    }

    private void checkSetWest(double west) {
        if (west < westernPoint || westernPoint == 0) {
            westernPoint = west;
        }
    }

    private void checkSetEast(double east) {
        if (east > easternPoint || easternPoint == 0) {
            easternPoint = east;
        }
    }

    private void checkSetMaxSpeed(double maxSpeed) {
        if (this.maxSpeed < maxSpeed) {
            this.maxSpeed = maxSpeed;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TrackEntity)) return false;
        TrackEntity entity = (TrackEntity) o;
        return getStartTime() == entity.getStartTime() &&
                Double.compare(entity.getDistance(), getDistance()) == 0 &&
                Double.compare(entity.getAscent(), getAscent()) == 0 &&
                Double.compare(entity.getDescent(), getDescent()) == 0 &&
                getElapsedTime() == entity.getElapsedTime() &&
                getNumOfTrackPoints() == entity.getNumOfTrackPoints() &&
                Double.compare(entity.getNorthestPoint(), getNorthestPoint()) == 0 &&
                Double.compare(entity.getSouthestPoint(), getSouthestPoint()) == 0 &&
                Double.compare(entity.getWesternPoint(), getWesternPoint()) == 0 &&
                Double.compare(entity.getEasternPoint(), getEasternPoint()) == 0 &&
                Double.compare(entity.getMinAltitude(), getMinAltitude()) == 0 &&
                Double.compare(entity.getMaxAltitude(), getMaxAltitude()) == 0 &&
                Double.compare(entity.getMaxSpeed(), getMaxSpeed()) == 0 &&
                Double.compare(entity.getAvgSpeed(), getAvgSpeed()) == 0 &&
                Objects.equals(getFirebaseId(), entity.getFirebaseId()) &&
                Objects.equals(getTitle(), entity.getTitle()) &&
                Objects.equals(getMetadata(), entity.getMetadata());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTrackId(), getFirebaseId(), getTitle(), getStartTime(), getDistance(), getAscent(), getDescent(), getElapsedTime(), getNumOfTrackPoints(), getNorthestPoint(), getSouthestPoint(), getWesternPoint(), getEasternPoint(), getMinAltitude(), getMaxAltitude(), getMaxSpeed(), getAvgSpeed(), getMetadata());
    }

    @NonNull
    @Override
    public String toString() {
        return "TrackEntity: id: "+ trackId+ " firebaseId: "+ firebaseId + " name: "+ title;
    }

    public static String getDefaultName(Context context, long startTime) {
        Calendar cl = Calendar.getInstance();
        cl.setTimeInMillis(startTime);
        int hour = cl.get(Calendar.HOUR_OF_DAY);
        if (hour <= 3 || hour > 21) {
            return context.getString(R.string.night_track);
        } else if (hour < 6) {
            return context.getString(R.string.dawn_track);
        } else if (hour < 8) {
            return context.getString(R.string.early_morning_track);
        } else if (hour < 12) {
            return context.getString(R.string.morning_track);
        } else if (hour < 18) {
            return context.getString(R.string.afternoon_track);
        } else {
            return context.getString(R.string.evening_track);
        }
    }

    public static TrackEntity fromTrackWithPoints(TrackWithPoints trackWithPoints) {
        TrackEntity entity = new TrackEntity();
        entity.setTrackId(trackWithPoints.getTrackId());
        entity.setFirebaseId(trackWithPoints.getFirebaseId());
        entity.setTitle(trackWithPoints.getTitle());
        entity.setStartTime(trackWithPoints.getStartTime());
        entity.setDistance(trackWithPoints.getDistance());
        entity.setAscent(trackWithPoints.getAscent());
        entity.setDescent(trackWithPoints.getDescent());
        entity.setElapsedTime(trackWithPoints.getElapsedTime());
        entity.setNumOfTrackPoints(trackWithPoints.getNumOfTrackPoints());
        entity.setNorthestPoint(trackWithPoints.getNorthestPoint());
        entity.setSouthestPoint(trackWithPoints.getSouthestPoint());
        entity.setWesternPoint(trackWithPoints.getWesternPoint());
        entity.setEasternPoint(trackWithPoints.getEasternPoint());
        entity.setMinAltitude(trackWithPoints.getMinAltitude());
        entity.setMaxAltitude(trackWithPoints.getMaxAltitude());
        entity.setMaxSpeed(trackWithPoints.getMaxSpeed());
        entity.setAvgSpeed(trackWithPoints.getAvgSpeed());
        entity.setMetadata(trackWithPoints.getMetadata());
        return entity;
    }
}
