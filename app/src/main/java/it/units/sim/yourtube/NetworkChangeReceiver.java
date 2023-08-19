package it.units.sim.yourtube;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class NetworkChangeReceiver extends BroadcastReceiver {

    private final MutableLiveData<Boolean> networkAvailability = new MutableLiveData<>();

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();

        if (activeNetwork != null && activeNetwork.isConnected()) {
            networkAvailability.postValue(true);
        } else {
            networkAvailability.postValue(false);
        }
    }

    public LiveData<Boolean> getNetworkAvailability() {
        return networkAvailability;
    }
}
