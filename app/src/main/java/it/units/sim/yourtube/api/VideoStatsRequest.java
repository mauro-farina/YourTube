package it.units.sim.yourtube.api;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.VideoListResponse;
import com.google.api.services.youtube.model.VideoStatistics;

import java.io.IOException;
import java.security.InvalidParameterException;

public class VideoStatsRequest extends AbstractYouTubeRequest<VideoStatistics> {

    private final String videoId;

    public VideoStatsRequest(GoogleAccountCredential credential,
                             Callback<VideoStatistics> callback,
                             String videoId) {
        super(credential, callback);
        this.videoId = videoId;
    }

    @Override
    protected Result<VideoStatistics> performRequest() throws IOException {
        YouTube.Videos.List request = youtubeService
                .videos()
                .list("statistics")
                .setId(videoId);

        VideoListResponse response = request.execute();

        if (response.getItems().size() == 0) {
            return new Result.Error<>(new InvalidParameterException("Video not found"));
        }

        return new Result.Success<>(response.getItems().get(0).getStatistics());
    }
}
