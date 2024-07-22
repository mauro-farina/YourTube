package it.units.sim.yourtube.data;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.room.Room;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import it.units.sim.yourtube.YourTubeApp;
import it.units.sim.yourtube.model.WatchData;

public class WatchDataViewModel extends AndroidViewModel {

    private final LiveData<List<WatchData>> watchData;
    private final WatchDataDAO dao;
    private final ExecutorService executorService;
    private final String owner;

    public WatchDataViewModel(@NonNull Application application) {
        super(application);
        YourTubeApp app = getApplication();
        executorService = app.getExecutorService();
        WatchDatabase db = Room
                .databaseBuilder(
                        application.getApplicationContext(),
                        WatchDatabase.class,
                        "watchdata-db")
                .build();
        dao = db.watchDataDAO();
        owner = app.getGoogleCredential().getSelectedAccountName();
        watchData = dao.getAll(owner);
    }

    public LiveData<List<WatchData>> get() {
        return watchData;
    }

    public WatchData find(String videoId) {
        Callable<WatchData> task = () -> dao.findByName(videoId, owner);
        Future<WatchData> future = executorService.submit(task);

        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }

//    public float getTimestamp(String videoId) {
//        if (watchData.getValue() == null) {
//            return 0;
//        }
//        for (WatchData data : watchData.getValue()) {
//            if (data.getVideoId().equals(videoId)) {
//                return data.getTimestamp();
//            }
//        }
//        return 0;
//    }

    public void add(String videoId, float timestamp, boolean watched) {
        WatchData data = new WatchData(videoId, timestamp, watched, owner);
        add(data);
    }

    public void add(WatchData data) {
        executorService.submit(() -> dao.insertAll(data));
    }

    public void delete(WatchData data) {
        executorService.submit(() -> dao.delete(data));
    }

    public void update(WatchData data) {
        executorService.submit(() -> dao.updateAll(data));
    }

    public void deleteAll() {
        executorService.submit(() -> dao.deleteAll(owner));
    }

}
