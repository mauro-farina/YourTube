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

    @Query("SELECT * FROM categories")
    LiveData<List<Category>> getAll();

    @Query("SELECT * FROM categories WHERE name LIKE :name LIMIT 1")
    LiveData<Category> findByName(String name);

    @Insert
    void insertAll(Category... categories);

    @Delete
    void delete(Category category);

    @Query("DELETE FROM categories")
    void deleteAll();

    @Update
    void updateAll(Category... categories);

}
