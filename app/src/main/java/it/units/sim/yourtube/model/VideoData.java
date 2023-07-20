package it.units.sim.yourtube.model;

import com.google.api.client.util.DateTime;
import com.google.api.services.youtube.model.PlaylistItemSnippet;

public class VideoData {
    private final String title;
    private final String videoId;
    private final String thumbnailUrl;
    private final DateTime publishedAt;
    private final UserSubscription subscription;

    public VideoData(PlaylistItemSnippet playlistItemSnippet, UserSubscription subscription) {
        this.title = playlistItemSnippet.getTitle();
        this.videoId = playlistItemSnippet.getResourceId().getVideoId();
        this.thumbnailUrl = playlistItemSnippet.getThumbnails().getMaxres().getUrl();
        this.publishedAt = playlistItemSnippet.getPublishedAt();
        this.subscription = subscription;
    }

    public String getTitle() {
        return title;
    }

    public String getVideoId() {
        return videoId;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public DateTime getPublishedAt() {
        return publishedAt;
    }

    public UserSubscription getChannel(){
        return subscription;
    }
}
