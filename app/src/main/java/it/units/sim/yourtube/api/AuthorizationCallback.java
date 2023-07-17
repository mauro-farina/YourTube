package it.units.sim.yourtube.api;

import android.content.Intent;

public interface AuthorizationCallback {
    void onMissingAuthorization(Intent intent);
    default void onGrantedAuthorization() { }
}
