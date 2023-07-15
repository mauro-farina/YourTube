package it.units.sim.yourtube;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;

public class GoogleCredentialManager {
    private static GoogleCredentialManager instance;
    private GoogleAccountCredential credential;

    private GoogleCredentialManager() {
    }

    public static GoogleCredentialManager getInstance() {
        if (instance == null) {
            instance = new GoogleCredentialManager();
        }
        return instance;
    }

    public GoogleAccountCredential getCredential() {
        return credential;
    }

    public void setCredential(GoogleAccountCredential credential) {
        this.credential = credential;
    }

}
