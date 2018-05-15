package com.awolity.trakr.data.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.awolity.trakr.data.entity.TrackEntity;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface TrackDao {

    @Insert(onConflict = REPLACE)
    long save(TrackEntity trackEntity);

    @Update(onConflict = REPLACE)
    void update(TrackEntity trackEntity);

    @Query("SELECT * FROM track_table ORDER BY start_time")
    LiveData<List<TrackEntity>> loadAll();

    @Query("SELECT * FROM track_table")
    List<TrackEntity> loadAllSync();

    @Query("SELECT * FROM track_table WHERE track_id = :trackId")
    LiveData<TrackEntity> loadById(long trackId);

    @Query("SELECT num_of_trackpoints FROM track_table WHERE track_id = :trackId")
    LiveData<Integer> loadNumOftrackpointsById(long trackId);

    @Query("SELECT * FROM track_table WHERE track_id = :trackId")
    TrackEntity loadByIdSync(long trackId);

    @Insert(onConflict = REPLACE)
    void saveAll(List<TrackEntity> trackEntities);

    @Delete
    void delete(TrackEntity trackEntity);

    @Delete
    void deleteAll(List<TrackEntity> trackEntities);
}
