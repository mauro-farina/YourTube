package it.units.sim.yourtube.data;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Room;

import com.tencent.mmkv.MMKV;

import java.util.List;
import java.util.concurrent.ExecutorService;

import it.units.sim.yourtube.YourTubeApp;
import it.units.sim.yourtube.model.Category;
import it.units.sim.yourtube.model.VideoData;

public class WatchLaterViewModel extends AndroidViewModel {

    private final MutableLiveData<List<VideoData>> videosLiveData = new MutableLiveData<>();
//    private final ExecutorService executorService;
    private final WatchLaterDatabase watchLaterDB;

    public WatchLaterViewModel(@NonNull Application application) {
        super(application);
        YourTubeApp app = getApplication();
//        executorService = app.getExecutorService();
        String owner = app.getGoogleCredential().getSelectedAccountName();
        watchLaterDB = new WatchLaterDatabase(owner);
        updateList();
    }

    private void updateList() {
        videosLiveData.setValue(watchLaterDB.getVideos());
    }

    public LiveData<List<VideoData>> getVideosLiveData() {
        return videosLiveData;
    }

    public void addVideo(VideoData video) {
        watchLaterDB.addVideo(video);
        updateList();
    }

}
