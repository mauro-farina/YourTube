package it.units.sim.yourtube;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

public class NetworkChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();

        if (activeNetwork != null && activeNetwork.isConnectedOrConnecting()) {
            // Network is available or connecting
            if (activeNetwork.isConnected()) {
                // Internet connection is back
                Toast.makeText(context, "Internet connection is back", Toast.LENGTH_SHORT).show();
            } else {
                // Connected to a network, but internet might not be available yet
                Toast.makeText(context, "Connected to network, but internet might not be available yet", Toast.LENGTH_SHORT).show();
            }
        } else {
            // No network available
            Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show();
        }
    }

}
