package it.units.sim.yourtube.api;

import android.content.Intent;

public interface MissingAuthorizationCallback {
    void onMissingAuthorization(Intent intent);
}
