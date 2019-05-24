package com.awolity.trakr.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.awolity.trakr.data.entity.TrackEntity;
import com.awolity.trakr.data.entity.TrackWithPoints;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface TrackDao {

    @Insert(onConflict = REPLACE)
    long save(TrackEntity trackEntity);

    @Update(onConflict = REPLACE)
    void update(TrackEntity trackEntity);

    @Query("SELECT * FROM track_table ORDER BY start_time DESC")
    LiveData<List<TrackEntity>> loadAll();

    @Query("SELECT * FROM track_table ORDER BY start_time DESC")
    List<TrackEntity> loadAllSync();

    @Query("SELECT * FROM track_table WHERE track_id = :trackId")
    LiveData<TrackEntity> loadById(long trackId);

    @Query("SELECT * FROM track_table WHERE track_id = :trackId")
    TrackEntity loadByIdSync(long trackId);

    @Transaction
    @Query("SELECT * FROM track_table ORDER BY start_time DESC")
    LiveData<List<TrackWithPoints>> loadAllWithPoints();

    @Transaction
    @Query("SELECT * FROM track_table WHERE track_id = :trackId")
    TrackWithPoints loadByIdWithPointsSync(long trackId);

    @Query("DELETE FROM track_table WHERE track_id = :trackId")
    void delete(long trackId);

    @Query("DELETE FROM track_table WHERE firebase_id = :firebaseId")
    void delete(String firebaseId);
}
