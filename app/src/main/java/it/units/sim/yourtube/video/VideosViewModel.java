package it.units.sim.yourtube.video;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.Date;

import it.units.sim.yourtube.model.Category;

public class VideosViewModel extends ViewModel {

    private final MutableLiveData<Category> categoryFilter = new MutableLiveData<>();
    private final MutableLiveData<Date> dateFilter = new MutableLiveData<>(new Date());

    public LiveData<Category> getCategoryFilter() {
        return categoryFilter;
    }

    public LiveData<Date> getDateFilter() {
        return dateFilter;
    }

    public void setCategoryFilter(Category category) {
        categoryFilter.setValue(category);
    }

    public void setDateFilter(@NonNull Date date) {
        dateFilter.setValue(date);
    }

}
