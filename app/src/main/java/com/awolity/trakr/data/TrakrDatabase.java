package com.awolity.trakr.data;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;

import com.awolity.trakr.data.dao.TrackDao;
import com.awolity.trakr.data.dao.TrackpointDao;
import com.awolity.trakr.data.entity.TrackEntity;
import com.awolity.trakr.data.entity.TrackpointEntity;

@Database(entities = {TrackEntity.class, TrackpointEntity.class},
version = 10)
public abstract class TrakrDatabase extends RoomDatabase {

    private static final String DATABASE_NAME = "trakr-db.db";
    private static TrakrDatabase INSTANCE;

    public abstract TrackDao trackDao();

    public abstract TrackpointDao trackPointDao();

    public static TrakrDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (TrakrDatabase.class) {
                if (INSTANCE == null) {
                    // Create database here
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            TrakrDatabase.class, DATABASE_NAME)
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}

