package com.awolity.trakr.data.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.awolity.trakr.data.entity.TrackpointEntity;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.IGNORE;
import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface TrackpointDao {

    @SuppressWarnings("UnusedReturnValue")
    @Insert(onConflict = REPLACE)
    long save(TrackpointEntity trackpointEntity);

    @Insert(onConflict = IGNORE)
    void saveAll(List<TrackpointEntity> trackpointEntities);

    @Query("SELECT * FROM trackpoint_table WHERE track_id = :trackId ORDER BY time")
    LiveData<List<TrackpointEntity>> loadByTrack(long trackId);

    @Query("SELECT * FROM trackpoint_table WHERE track_id = :trackId ORDER BY time DESC LIMIT 1")
    LiveData<TrackpointEntity> loadActualTrackpointByTrack(long trackId);

}
