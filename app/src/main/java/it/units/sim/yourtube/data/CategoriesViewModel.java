package it.units.sim.yourtube.data;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.room.Room;

import java.util.List;
import java.util.concurrent.ExecutorService;

import it.units.sim.yourtube.GoogleCredentialManager;
import it.units.sim.yourtube.YourTubeApp;
import it.units.sim.yourtube.data.CategoryDAO;
import it.units.sim.yourtube.data.LocalDatabase;
import it.units.sim.yourtube.model.Category;

public class CategoriesViewModel extends AndroidViewModel {

    private final LiveData<List<Category>> categoriesList;
    private final CategoryDAO categoryDao;
    private final ExecutorService executorService;

    public CategoriesViewModel(@NonNull Application application) {
        super(application);
        YourTubeApp app = getApplication();
        executorService = app.getExecutorService();
        LocalDatabase db = Room
                .databaseBuilder(
                        application.getApplicationContext(),
                        LocalDatabase.class,
                        "categories-db")
                .build();
        categoryDao = db.categoryDao();
        String categoriesOwner = GoogleCredentialManager.getInstance().getCredential().getSelectedAccountName();
        categoriesList = categoryDao.getAll(categoriesOwner);
    }

    public LiveData<List<Category>> getCategoriesList() {
        return categoriesList;
    }

    public void addCategory(Category category) {
        executorService.submit(() -> categoryDao.insertAll(category));
    }

    public void deleteCategory(Category category) {
        executorService.submit(() -> categoryDao.delete(category));
    }

    public void updateCategory(Category category) {
        executorService.submit(() -> categoryDao.updateAll(category));
    }

}
