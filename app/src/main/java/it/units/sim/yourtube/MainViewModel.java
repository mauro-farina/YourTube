package it.units.sim.yourtube;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;

import java.util.ArrayList;
import java.util.List;

import it.units.sim.yourtube.api.RequestCallback;
import it.units.sim.yourtube.api.RequestThread;
import it.units.sim.yourtube.api.SubscriptionListRequest;
import it.units.sim.yourtube.model.UserSubscription;

public class MainViewModel extends ViewModel {

    private final MutableLiveData<List<UserSubscription>> subscriptionsList = new MutableLiveData<>();

    public void fetchUserSubscriptions() {
        GoogleAccountCredential credential = GoogleCredentialManager.getInstance().getCredential();
        SubscriptionListRequest subscriptionRequest = new SubscriptionListRequest(credential);
        RequestCallback<List<UserSubscription>> subscriptionListCallback = subscriptionsList::setValue;

        RequestThread<List<UserSubscription>> rThread =
                new RequestThread<>(subscriptionRequest, subscriptionListCallback);
        rThread.start();
    }

    public LiveData<List<UserSubscription>> getSubscriptionsList() {
        if (subscriptionsList.getValue() == null) {
            subscriptionsList.setValue(new ArrayList<>());
        }
        return subscriptionsList;
    }

}
