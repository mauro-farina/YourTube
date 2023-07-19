package it.units.sim.yourtube.model;

import com.google.api.client.util.DateTime;
import com.google.api.services.youtube.model.PlaylistItemSnippet;

public class VideoData {
    private final String title;
    private final String videoId;
    private final String thumbnailUrl;
    private final DateTime publishedAt;

    public VideoData(PlaylistItemSnippet playlistItemSnippet) {
        this.title = playlistItemSnippet.getTitle();
        this.videoId = playlistItemSnippet.getResourceId().getVideoId();
        this.thumbnailUrl = playlistItemSnippet.getThumbnails().getStandard().getUrl();
        this.publishedAt = playlistItemSnippet.getPublishedAt();
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
}
