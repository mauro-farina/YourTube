package it.units.sim.yourtube.data;

import androidx.room.AutoMigration;
import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import it.units.sim.yourtube.model.Category;
import it.units.sim.yourtube.model.Playlist;

@Database(
        entities = {Category.class, Playlist.class},
        autoMigrations = {
                @AutoMigration(from = 1, to = 2)
        },
        version = 2)
@TypeConverters({RoomTypeConverter.class})
public abstract class LocalDatabase extends RoomDatabase {
    public abstract CategoryDAO categoryDao();
    public abstract PlaylistDAO playlistDAO();
}
