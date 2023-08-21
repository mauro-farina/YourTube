package it.units.sim.yourtube;

import android.app.Application;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.google.firebase.FirebaseApp;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class YourTubeApp extends Application {
    private static final int NUM_THREADS = 64;
    private ExecutorService executorService;

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

    public ExecutorService getExecutorService() {
        return executorService;
    }
}
