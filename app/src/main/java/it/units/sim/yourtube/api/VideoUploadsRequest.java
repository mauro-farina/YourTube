package it.units.sim.yourtube.api;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.util.DateTime;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.PlaylistItem;
import com.google.api.services.youtube.model.PlaylistItemListResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import it.units.sim.yourtube.model.UserSubscription;
import it.units.sim.yourtube.model.VideoData;

public class VideoUploadsRequest extends AbstractYouTubeRequest<List<VideoData>> {

    private static final long MAX_RESULTS = 35;
    private final UserSubscription subscription;
    private final Date publishedOn;

    public VideoUploadsRequest(GoogleAccountCredential credential,
                               Callback<List<VideoData>> callback,
                               UserSubscription subscription,
                               Date publishedOn) {
        super(credential, callback);
        this.subscription = subscription;
        this.publishedOn = publishedOn;
    }

    @Override
    protected Result<List<VideoData>> performRequest() throws IOException {
        String playlistId = subscription.getUploadsPlaylistId();

        YouTube.PlaylistItems.List videosRequest = youtubeService
                .playlistItems()
                .list("snippet")
                .setMaxResults(MAX_RESULTS)
                .setPlaylistId(playlistId);

        PlaylistItemListResponse response = videosRequest.execute();

        List<PlaylistItem> playlistItems = response.getItems();
        int results = playlistItems.size();
        if (results == 0) {
            return new Result.Success<>(new ArrayList<>());
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(publishedOn);

        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        DateTime publishedAfter = new DateTime(calendar.getTime());

        calendar.add(Calendar.DATE, 1);
        calendar.add(Calendar.MILLISECOND, -1);
        DateTime publishedBefore = new DateTime(calendar.getTime());

        DateTime firstPlaylistItemPublishDateTime = playlistItems
                .get(0)
                .getSnippet()
                .getPublishedAt();
        DateTime lastPlaylistItemPublishDateTime = playlistItems
                .get(results-1)
                .getSnippet()
                .getPublishedAt();

        if (firstPlaylistItemPublishDateTime.getValue() < publishedAfter.getValue()) {
            return new Result.Success<>(new ArrayList<>());
        }

        if (lastPlaylistItemPublishDateTime.getValue() > publishedAfter.getValue()) {
            recursiveWorker(playlistItems, response.getNextPageToken(), publishedAfter);
        }

        List<VideoData> fetchedVideos = playlistItems
                .stream()
                .filter(i -> i.getSnippet().getPublishedAt().getValue() > publishedAfter.getValue())
                .filter(i -> i.getSnippet().getPublishedAt().getValue() < publishedBefore.getValue())
                .map(i -> new VideoData(i.getSnippet(), subscription))
                .collect(Collectors.toList());

        return new Result.Success<>(fetchedVideos);
    }

    private void recursiveWorker(List<PlaylistItem> list, String nextPageToken, DateTime publishedAfter) throws IOException {
        String playlistId = subscription.getUploadsPlaylistId();

        YouTube.PlaylistItems.List videosRequest = youtubeService
                .playlistItems()
                .list("snippet")
                .setMaxResults(MAX_RESULTS)
                .setPageToken(nextPageToken)
                .setPlaylistId(playlistId);

        PlaylistItemListResponse response = videosRequest.execute();
        List<PlaylistItem> playlistItems = response.getItems();

        int results = playlistItems.size();
        if (results == 0) {
            return;
        }
        list.addAll(playlistItems);

        DateTime lastPlaylistItemPublishDateTime = playlistItems
                .get(results-1)
                .getSnippet()
                .getPublishedAt();

        if (lastPlaylistItemPublishDateTime.getValue() > publishedAfter.getValue()) {
            recursiveWorker(list, response.getNextPageToken(), publishedAfter);
        }
    }

}
