package it.units.sim.yourtube;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;

import it.units.sim.yourtube.api.RequestCallback;
import it.units.sim.yourtube.api.Result;
import it.units.sim.yourtube.api.RequestThread;
import it.units.sim.yourtube.api.SubscriptionListRequest;
import it.units.sim.yourtube.api.VideoUploadsRequest;
import it.units.sim.yourtube.model.Category;
import it.units.sim.yourtube.model.UserSubscription;
import it.units.sim.yourtube.model.VideoData;

public class MainViewModel extends AndroidViewModel {

    private final ExecutorService executorService;
    private final MutableLiveData<List<UserSubscription>> subscriptionsList;
    private final MutableLiveData<List<VideoData>> videosList;
    private final MutableLiveData<Category> categoryFilter;

    public MainViewModel(@NonNull Application application) {
        super(application);
        YourTubeApp app = getApplication();
        executorService = app.getExecutorService();
        subscriptionsList = new MutableLiveData<>(new ArrayList<>());
        videosList = new MutableLiveData<>(new ArrayList<>());
        categoryFilter = new MutableLiveData<>();
    }

    public void fetchUserSubscriptions() {
        GoogleAccountCredential credential = GoogleCredentialManager.getInstance().getCredential();
        executorService.submit(new SubscriptionListRequest(credential, result -> {
            if (result instanceof Result.Success) {
                List<UserSubscription> fetchedSubscriptions = ((Result.Success<List<UserSubscription>>) result).getData();
                subscriptionsList.postValue(fetchedSubscriptions); // Does not work (with no exceptions thrown)
            } else {
                // error
                System.out.println("Request Failed");
                System.out.println(((Result.Error<List<UserSubscription>>) result).getException().getMessage());
            }
        }));
    }

    public void fetchVideos(Date date) {
        videosList.setValue(new ArrayList<>());
        GoogleAccountCredential credential = GoogleCredentialManager.getInstance().getCredential();
        for (UserSubscription sub : Objects.requireNonNull(subscriptionsList.getValue())) {
            VideoUploadsRequest subscriptionRequest = new VideoUploadsRequest(credential, sub, date);
            RequestCallback<List<VideoData>> subscriptionListCallback = list -> {
                List<VideoData> videos = videosList.getValue();
                if (videos != null) {
                    videos.addAll(list);
                    videosList.setValue(videos);
                }
            };
            RequestThread<List<VideoData>> rThread =
                    new RequestThread<>(subscriptionRequest, subscriptionListCallback);
            rThread.start();
        }
    }

    public void setCategoryFilter(Category category) {
        categoryFilter.setValue(category);
    }

    public LiveData<List<UserSubscription>> getSubscriptionsList() {
        return subscriptionsList;
    }

    public LiveData<List<VideoData>> getVideosList() {
        return videosList;
    }

    public LiveData<Category> getCategoryFilter() {
        return categoryFilter;
    }

}
