package it.units.sim.yourtube.utils;

import android.net.ConnectivityManager;
import android.net.Network;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class NetworkAvailabilityCallback extends ConnectivityManager.NetworkCallback {

    private final MutableLiveData<Boolean> networkAvailability = new MutableLiveData<>();

    public NetworkAvailabilityCallback() {
        networkAvailability.postValue(false);
    }

    @Override
    public void onAvailable(Network network) {
        networkAvailability.postValue(true);
    }

    @Override
    public void onLost(Network network) {
        networkAvailability.postValue(false);
    }

    public LiveData<Boolean> getNetworkAvailability() {
        return networkAvailability;
    }
}
