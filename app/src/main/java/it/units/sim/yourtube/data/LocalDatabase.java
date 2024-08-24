package it.units.sim.yourtube.data;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import it.units.sim.yourtube.model.Category;
import it.units.sim.yourtube.model.Playlist;

@Database(entities = {Category.class, Playlist.class}, version = 1, exportSchema = false)
@TypeConverters({CategoryConverter.class})
public abstract class LocalDatabase extends RoomDatabase {
    public abstract CategoryDAO categoryDao();
    public abstract PlaylistDAO playlistDAO();
}
