package it.units.sim.yourtube;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

public class NetworkChangeReceiver extends BroadcastReceiver {

    private static boolean first = true;

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();

        if (first) {
            first = false;
            return;
        }

        if (activeNetwork != null && activeNetwork.isConnected()) {
            Toast.makeText(context, "Internet connection is back", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show();
        }
    }

}
