package it.units.sim.yourtube;

import android.app.Application;

import com.google.firebase.FirebaseApp;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class YourTubeApp extends Application {
    private ExecutorService executorService;
    private static final int NUM_THREADS = 64;

    @Override
    public void onCreate() {
        super.onCreate();
        executorService = Executors.newFixedThreadPool(NUM_THREADS);
        FirebaseApp.initializeApp(this);
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
