package it.units.sim.yourtube.data;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import it.units.sim.yourtube.model.WatchData;

@Database(entities = {WatchData.class}, version = 1, exportSchema = false)
public abstract class WatchDatabase extends RoomDatabase {
    public abstract WatchDataDAO watchDataDAO();
}
