package it.units.sim.yourtube.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import it.units.sim.yourtube.model.WatchData;

@Dao
public interface WatchDataDAO {

    @Query("SELECT * FROM watchdata WHERE owner LIKE :owner")
    LiveData<List<WatchData>> getAll(String owner);

    @Query("SELECT * FROM watchdata WHERE video_id LIKE :videoId AND owner LIKE :owner LIMIT 1")
    WatchData findByName(String videoId, String owner);

    @Insert
    void insertAll(WatchData... watchData);

    @Delete
    void delete(WatchData watchData);

    @Query("DELETE FROM watchdata WHERE owner LIKE :owner")
    void deleteAll(String owner);

    @Update
    void updateAll(WatchData... watchData);

}
