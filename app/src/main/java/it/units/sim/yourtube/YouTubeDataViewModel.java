package it.units.sim.yourtube;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
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

public class YouTubeDataViewModel extends AndroidViewModel {

    private final ExecutorService executorService;
    private final MutableLiveData<List<UserSubscription>> subscriptionsList;
    private final MutableLiveData<List<VideoData>> videosList;
    private final MutableLiveData<Boolean> missingYouTubeDataAuthorization;
    private final MutableLiveData<Boolean> quotaExceeded;
    private final GoogleAccountCredential credential;

    public YouTubeDataViewModel(@NonNull Application application) {
        super(application);
        YourTubeApp app = getApplication();
        credential = app.getGoogleCredential();
        executorService = app.getExecutorService();
        subscriptionsList = new MutableLiveData<>(new LinkedList<>());
        videosList = new MutableLiveData<>(new LinkedList<>());
        missingYouTubeDataAuthorization = new MutableLiveData<>();
        quotaExceeded = new MutableLiveData<>();
    }

    public void fetchUserSubscriptions() {
        executorService.submit(new SubscriptionListRequest(credential, result -> {
            if (result instanceof Result.Success) {
                List<UserSubscription> fetchedSubscriptions = ((Result.Success<List<UserSubscription>>) result).getData();
                fetchedSubscriptions.sort(Comparator.comparing(UserSubscription::getChannelName));
                subscriptionsList.postValue(fetchedSubscriptions);
            } else {
                handleResultError((Result.Error<?>) result);
            }
        }));
    }

    private void handleResultError(Result.Error<?> result) {
        Exception exception = result.getException();
        Throwable cause = exception.getCause();
        if (exception.getMessage() != null
                && exception.getMessage().contains("quotaExceeded")) {
            quotaExceeded.postValue(true);
        }
        if (cause != null && cause.getMessage() != null
                && cause.getMessage().equals("NeedRemoteConsent")) {
            missingYouTubeDataAuthorization.postValue(true);
        }
    }

    private final List<Future<?>> ongoingFetchTasks = new ArrayList<>();

    private void cancelOngoingTasks() {
        for (Future<?> task : ongoingFetchTasks) {
            task.cancel(true);
        }
        ongoingFetchTasks.clear();
    }

    public void fetchVideos(Date date, Category category) {
        cancelOngoingTasks();
        videosList.setValue(new LinkedList<>());
        for (UserSubscription sub : Objects.requireNonNull(subscriptionsList.getValue())) {
            if (category != null && !category.getChannelIds().contains(sub.getChannelId()))
                continue;
            Future<?> task = executorService.submit(new VideoUploadsRequest(credential, result -> {
                if (result instanceof Result.Success) {
                    List<VideoData> fetchedVideos = ((Result.Success<List<VideoData>>) result).getData();
                    List<VideoData> videos = videosList.getValue();
                    if (videos != null) {
                        videos.addAll(fetchedVideos);
                        videos.sort(Comparator.comparingLong(VideoData::getPublishedDateInMillis).reversed());
                        videosList.postValue(videos);
                    }
                } else {
                    handleResultError((Result.Error<?>) result);
                }
            }, sub, date));
            ongoingFetchTasks.add(task);
        }
    }

    public LiveData<List<UserSubscription>> getSubscriptionsList() {
        return subscriptionsList;
    }

    public LiveData<List<VideoData>> getVideosList() {
        return videosList;
    }

    public LiveData<Boolean> getMissingYouTubeDataAuthorization() {
        return missingYouTubeDataAuthorization;
    }

    public LiveData<Boolean> getQuotaExceeded() {
        return quotaExceeded;
    }

}
