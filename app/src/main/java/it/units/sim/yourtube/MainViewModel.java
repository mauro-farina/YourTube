package it.units.sim.yourtube;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import it.units.sim.yourtube.api.RequestCallback;
import it.units.sim.yourtube.api.RequestThread;
import it.units.sim.yourtube.api.SubscriptionListRequest;
import it.units.sim.yourtube.api.VideoUploadsRequest;
import it.units.sim.yourtube.model.UserSubscription;
import it.units.sim.yourtube.model.VideoData;

public class MainViewModel extends ViewModel {

    private final MutableLiveData<List<UserSubscription>>
            subscriptionsList = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<VideoData>>
            videosList = new MutableLiveData<>(new ArrayList<>());

    public void fetchUserSubscriptions() {
        GoogleAccountCredential credential = GoogleCredentialManager.getInstance().getCredential();
        SubscriptionListRequest subscriptionRequest = new SubscriptionListRequest(credential);
        RequestCallback<List<UserSubscription>> subscriptionListCallback = subscriptionsList::setValue;

        RequestThread<List<UserSubscription>> rThread =
                new RequestThread<>(subscriptionRequest, subscriptionListCallback);
        rThread.start();
    }

    public void fetchVideos() {
        videosList.setValue(new ArrayList<>());
        GoogleAccountCredential credential = GoogleCredentialManager.getInstance().getCredential();
        for (UserSubscription sub : Objects.requireNonNull(subscriptionsList.getValue())) {
            VideoUploadsRequest subscriptionRequest = new VideoUploadsRequest(credential, sub, new Date());
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

    public LiveData<List<UserSubscription>> getSubscriptionsList() {
        return subscriptionsList;
    }

    public LiveData<List<VideoData>> getVideosList() {
        return videosList;
    }

}
