package it.units.sim.yourtube.videoplayer;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.services.youtube.model.VideoStatistics;

import java.math.BigInteger;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;

import it.units.sim.yourtube.R;
import it.units.sim.yourtube.YourTubeApp;
import it.units.sim.yourtube.api.Result;
import it.units.sim.yourtube.api.VideoCommentsRequest;
import it.units.sim.yourtube.api.VideoStatsRequest;
import it.units.sim.yourtube.model.VideoComment;

public class VideoPlayerViewModel extends AndroidViewModel {

    private final ExecutorService executorService;
    private final MutableLiveData<String> viewsCount;
    private final MutableLiveData<String> likesCount;
    private final MutableLiveData<List<VideoComment>> comments;
    private final GoogleAccountCredential credential;

    public VideoPlayerViewModel(@NonNull Application application) {
        super(application);
        YourTubeApp app = getApplication();
        credential = app.getGoogleCredential();
        executorService = app.getExecutorService();
        viewsCount = new MutableLiveData<>();
        likesCount = new MutableLiveData<>();
        comments = new MutableLiveData<>(new ArrayList<>());
    }

    public void setVideoId(String videoId) {
        executorService.submit(new VideoStatsRequest(
                credential,
                result -> {
                    if (result instanceof Result.Success) {
                        VideoStatistics stats = ((Result.Success<VideoStatistics>) result).getData();
                        BigInteger views = stats.getViewCount();
                        BigInteger likes = stats.getLikeCount();
                        String readableViewsCount = getHumanReadableNumber(views);
                        String readableLikesCount = getHumanReadableNumber(likes);
                        viewsCount.postValue(readableViewsCount);
                        likesCount.postValue(readableLikesCount);
                    } else {
                        viewsCount.postValue("N/A");
                        likesCount.postValue("N/A");
                    }
                },
                videoId
        ));
        executorService.submit(new VideoCommentsRequest(
                credential,
                result -> {
                    if (result instanceof Result.Success) {
                        comments.postValue(((Result.Success<List<VideoComment>>) result).getData());
                    } else {
                        comments.postValue(new ArrayList<>());
                    }
                },
                videoId,
                getApplication().getString(R.string.google_api_key)
        ));
    }

    public LiveData<String> getViewsCount() {
        return viewsCount;
    }

    public LiveData<String> getLikesCount() {
        return likesCount;
    }

    public LiveData<List<VideoComment>> getComments() {
        return comments;
    }

    private String getHumanReadableNumber(BigInteger bigNumber) {
        if (bigNumber == null)
            return "";

        double number = bigNumber.doubleValue();
        if (number > 1000000000)
            return formatBillions(number);
        else if (number > 1000000)
            return formatMillions(number);
        else if (number > 10000)
            return formatThousands(number);
        else
            return String.valueOf((int)number);
    }

    private String formatBillions(double number) {
        number = number / 1000000000;
        return String.format(Locale.getDefault(), "%.1f bln", number);
    }

    private String formatMillions(double number) {
        number = number / 1000000;
        return String.format(Locale.getDefault(), "%.1f mln", number);
    }

    private String formatThousands(double number) {
        NumberFormat numberFormat = NumberFormat.getInstance(Locale.getDefault());
        return numberFormat.format(number);
    }

}
