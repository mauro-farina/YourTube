package it.units.sim.yourtube.api;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.util.DateTime;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.PlaylistItem;
import com.google.api.services.youtube.model.PlaylistItemListResponse;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import it.units.sim.yourtube.model.UserSubscription;
import it.units.sim.yourtube.model.VideoData;

public class VideoUploadsRequest extends YouTubeApiRequest<List<VideoData>> {

    private static final long MAX_RESULTS = 5;

    private final UserSubscription subscription;
    private final Date publishedOn;

    public VideoUploadsRequest(GoogleAccountCredential credential,
                               UserSubscription subscription,
                               Date publishedOn) {
        super(credential);
        this.subscription = subscription;
        this.publishedOn = publishedOn;
    }

    @Override
    public List<VideoData> call() throws Exception {
        String playlistId = subscription.getUploadsPlaylistId();

        YouTube.PlaylistItems.List videosRequest = youtubeService
                .playlistItems()
                .list("snippet")
                .setMaxResults(MAX_RESULTS)
                .setPlaylistId(playlistId);

        PlaylistItemListResponse response = videosRequest.execute();

        int results = response.getItems().size();
        if (results == 0) {
            return new ArrayList<>();
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

        List<PlaylistItem> playlistItems = response.getItems();
        DateTime firstPlaylistItemPublishDateTime = playlistItems
                .get(0)
                .getContentDetails()
                .getVideoPublishedAt();
        DateTime lastPlaylistItemPublishDateTime = playlistItems
                .get(results-1)
                .getContentDetails()
                .getVideoPublishedAt();

        if (firstPlaylistItemPublishDateTime.getValue() < publishedAfter.getValue()) {
            return new ArrayList<>();
        }

        if (lastPlaylistItemPublishDateTime.getValue() > publishedBefore.getValue()) {
            // TODO: recursive call
            System.out.println("recursion");
        }

        return playlistItems
                .stream()
                .filter(i -> i.getContentDetails().getVideoPublishedAt().getValue() > publishedAfter.getValue())
                .filter(i -> i.getContentDetails().getVideoPublishedAt().getValue() <  publishedBefore.getValue())
                .map(i -> new VideoData(i.getSnippet()))
                .collect(Collectors.toList());
    }
}
