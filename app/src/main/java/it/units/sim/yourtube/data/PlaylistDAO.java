package it.units.sim.yourtube.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import it.units.sim.yourtube.model.Playlist;

@Dao
public interface PlaylistDAO {

    @Query("SELECT * FROM playlist WHERE owner LIKE :owner")
    LiveData<List<Playlist>> getAll(String owner);

    @Query("SELECT * FROM categories WHERE owner LIKE :owner AND name LIKE :name LIMIT 1")
    LiveData<Playlist> get(String owner, String name);

    @Insert
    void insertAll(Playlist... playlists);

    @Update
    void update(Playlist playlist);

    @Delete
    void delete(Playlist playlist);

    @Query("DELETE FROM playlist WHERE owner LIKE :owner")
    void deleteAll(String owner);

}
