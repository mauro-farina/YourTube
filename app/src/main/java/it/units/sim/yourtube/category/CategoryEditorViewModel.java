package it.units.sim.yourtube.category;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import it.units.sim.yourtube.model.UserSubscription;

public class CategoryEditorViewModel extends ViewModel {

    private final MutableLiveData<List<UserSubscription>> selectedChannels
            = new MutableLiveData<>(new ArrayList<>());

    public LiveData<List<UserSubscription>> getSelectedChannels() {
        return selectedChannels;
    }

    public void setSelectedChannels(List<UserSubscription> newSelectedChannels) {
        selectedChannels.setValue(newSelectedChannels);
    }

}
