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
import java.util.concurrent.Future;

import it.units.sim.yourtube.api.Result;
import it.units.sim.yourtube.api.SubscriptionListRequest;
import it.units.sim.yourtube.api.VideoUploadsRequest;
import it.units.sim.yourtube.model.Category;
import it.units.sim.yourtube.model.UserSubscription;
import it.units.sim.yourtube.model.VideoData;

public class MainViewModel extends AndroidViewModel {

    private final ExecutorService executorService;
    private final MutableLiveData<List<UserSubscription>> subscriptionsList;
    private final MutableLiveData<List<VideoData>> videosList;

    public MainViewModel(@NonNull Application application) {
        super(application);
        YourTubeApp app = getApplication();
        executorService = app.getExecutorService();
        subscriptionsList = new MutableLiveData<>(new ArrayList<>());
        videosList = new MutableLiveData<>(new ArrayList<>());
    }

    public void fetchUserSubscriptions() {
        System.out.println("fetch userSubscriptions! ***************");
        GoogleAccountCredential credential = GoogleCredentialManager.getInstance().getCredential();
        executorService.submit(new SubscriptionListRequest(credential, result -> {
            if (result instanceof Result.Success) {
                List<UserSubscription> fetchedSubscriptions = ((Result.Success<List<UserSubscription>>) result).getData();
                subscriptionsList.postValue(fetchedSubscriptions);
            } else {
                // error
                System.out.println("Request Failed");
                System.out.println(((Result.Error<List<UserSubscription>>) result).getException().getMessage());
            }
        }));
    }

    private final List<Future<?>> ongoingFetchTasks = new ArrayList<>();

    public void fetchVideos(Date date, Category category) {
        videosList.setValue(new ArrayList<>());
        for (Future<?> task : ongoingFetchTasks) {
            task.cancel(true); // Cancel the task
        }
        ongoingFetchTasks.clear(); // Clear the list of ongoing tasks
        GoogleAccountCredential credential = GoogleCredentialManager.getInstance().getCredential();
        if (category != null)  {
            Objects.requireNonNull(subscriptionsList.getValue())
                    .stream()
                    .filter(s -> category.getChannelIds().contains(s.getChannelId()))
                    .forEach(sub -> executorService.submit(new VideoUploadsRequest(credential, result -> {
                        if (result instanceof Result.Success) {
                            List<VideoData> fetchedVideos = ((Result.Success<List<VideoData>>) result).getData();
                            List<VideoData> videos = videosList.getValue();
                            if (videos != null) {
                                videos.addAll(fetchedVideos);
                                videosList.postValue(videos);
                            }
                        } else {
                            // error
                            System.out.println("Request Failed");
                            System.out.println(((Result.Error<List<VideoData>>) result).getException().getMessage());
                        }
                    }, sub, date)));
        } else {
            for (UserSubscription sub : Objects.requireNonNull(subscriptionsList.getValue())) {
                Future<?> task = executorService.submit(new VideoUploadsRequest(credential, result -> {
                    if (result instanceof Result.Success) {
                        List<VideoData> fetchedVideos = ((Result.Success<List<VideoData>>) result).getData();
                        List<VideoData> videos = videosList.getValue();
                        if (videos != null) {
                            videos.addAll(fetchedVideos);
                            videosList.postValue(videos);
                        }
                    } else {
                        // error
                        System.out.println("Request Failed");
                        System.out.println(((Result.Error<List<VideoData>>) result).getException().getMessage());
                    }
                }, sub, date));
                ongoingFetchTasks.add(task); // Add the task to the list of ongoing tasks
            }
        }
    }

    public LiveData<List<UserSubscription>> getSubscriptionsList() {
        return subscriptionsList;
    }

    public LiveData<List<VideoData>> getVideosList() {
        return videosList;
    }

}
