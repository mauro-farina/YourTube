package it.units.sim.yourtube.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import it.units.sim.yourtube.model.Category;

@Dao
public interface CategoryDAO {

    @Query("SELECT * FROM categories WHERE owner LIKE :owner")
    LiveData<List<Category>> getAll(String owner);

    @Query("SELECT * FROM categories WHERE name LIKE :name LIMIT 1")
    LiveData<Category> findByName(String name);

    @Insert
    void insertAll(Category... categories);

    @Delete
    void delete(Category category);

    @Query("DELETE FROM categories WHERE owner LIKE :owner")
    void deleteAll(String owner);

    @Update
    void updateAll(Category... categories);

    // Optionally... @Insert, @Delete and @Update can return a number
    // https://developer.android.com/training/data-storage/room/accessing-data#java

}
