package it.units.sim.yourtube;

import android.app.Application;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.youtube.YouTubeScopes;
import com.google.firebase.FirebaseApp;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class YourTubeApp extends Application {

    private static final String[] YOUTUBE_API_SCOPES = { YouTubeScopes.YOUTUBE_READONLY };
    private static final int NUM_THREADS = 64;
    private ExecutorService executorService;
    private GoogleAccountCredential googleCredential;

    @Override
    public void onCreate() {
        super.onCreate();
        executorService = Executors.newFixedThreadPool(NUM_THREADS);
        FirebaseApp.initializeApp(this);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String theme = sharedPreferences.getString(SettingsManager.PREFERENCE_THEME, SettingsManager.PREFERENCE_THEME_DEFAULT);
        SettingsManager.setTheme(theme);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        executorService.shutdown();
    }

    public void setGoogleCredentialAccount(String accountName) {
        this.googleCredential = GoogleAccountCredential
                .usingOAuth2(getApplicationContext(), Arrays.asList(YOUTUBE_API_SCOPES))
                .setBackOff(new ExponentialBackOff())
                .setSelectedAccountName(accountName);
    }

    public GoogleAccountCredential getGoogleCredential() {
        return googleCredential;
    }

    public ExecutorService getExecutorService() {
        return executorService;
    }
}
