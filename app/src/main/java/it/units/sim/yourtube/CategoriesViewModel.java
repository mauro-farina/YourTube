package it.units.sim.yourtube;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.room.Room;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import it.units.sim.yourtube.data.CategoryDAO;
import it.units.sim.yourtube.data.LocalDatabase;
import it.units.sim.yourtube.model.Category;

public class CategoriesViewModel extends AndroidViewModel {

    private LiveData<List<Category>> categoriesList;
    private final CategoryDAO categoryDao;
    private final ExecutorService executor;

    public CategoriesViewModel(@NonNull Application application) {
        super(application);
        executor = Executors.newFixedThreadPool(1);
        LocalDatabase db = Room
                .databaseBuilder(
                        application.getApplicationContext(),
                        LocalDatabase.class,
                        "categories-db")
                .build();
        categoryDao = db.categoryDao();
        fetchCategories();
    }

    public void fetchCategories() {
        Future<LiveData<List<Category>>> categoriesFuture = executor.submit(categoryDao::getAll);
        try {
            categoriesList = categoriesFuture.get();
        } catch (ExecutionException | InterruptedException e) {
            System.err.println(e.getMessage());
        }
    }

    public LiveData<List<Category>> getCategoriesList() {
        return categoriesList;
    }

    public void addCategory(Category category) {
        executor.submit(() -> categoryDao.insertAll(category));
    }

    public void deleteCategory(Category category) {
        executor.submit(() -> categoryDao.delete(category));
    }

    public void updateCategory(Category category) {
        executor.submit(() -> categoryDao.updateAll(category));
    }

}
