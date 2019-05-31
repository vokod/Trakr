package com.awolity.trakr.repository.local.model.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.awolity.trakr.repository.local.model.entity.TrackpointEntity;

import java.util.List;

import static androidx.room.OnConflictStrategy.IGNORE;
import static androidx.room.OnConflictStrategy.REPLACE;

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
