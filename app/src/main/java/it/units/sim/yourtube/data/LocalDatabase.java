package it.units.sim.yourtube.data;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import it.units.sim.yourtube.model.Category;

@Database(entities = {Category.class}, version = 1)
@TypeConverters({CategoryConverter.class})
public abstract class LocalDatabase extends RoomDatabase {
    public abstract CategoryDAO categoryDao();
}
