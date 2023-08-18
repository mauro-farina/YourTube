package it.units.sim.yourtube.api;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.CommentThreadListResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import it.units.sim.yourtube.model.VideoComment;

public class VideoCommentsRequest extends AbstractYouTubeRequest<List<VideoComment>> {

    private final String videoId;
    private final String API_KEY;

    public VideoCommentsRequest(GoogleAccountCredential credential,
                                   Callback<List<VideoComment>> callback,
                                   String videoId,
                                   String API_KEY) {
        super(credential, callback);
        this.videoId = videoId;
        this.API_KEY = API_KEY;
    }

    @Override
    protected Result<List<VideoComment>> performRequest() throws IOException {
        YouTube.CommentThreads.List request = youtubeServiceNoCredential
                .commentThreads()
                .list("snippet")
                .setKey(API_KEY)
                .setVideoId(videoId)
                .setMaxResults(20L)
                .setOrder("relevance");

        CommentThreadListResponse response = request.execute();

        if (response.getItems().size() == 0) {
            return new Result.Success<>(new ArrayList<>());
        }

        return new Result.Success<>(
                response
                    .getItems()
                    .stream()
                    .map(c -> new VideoComment(c.getSnippet().getTopLevelComment().getSnippet()))
                    .collect(Collectors.toList()));
    }
}
