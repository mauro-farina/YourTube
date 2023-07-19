package it.units.sim.yourtube.model;

import com.google.api.client.util.DateTime;
import com.google.api.services.youtube.model.PlaylistItemSnippet;

public class VideoData {
    private final String title;
    private final String videoId;
    private final String thumbnailUrl;
    private final DateTime publishedAt;

    public VideoData(String title, String videoId, String thumbnailUrl, DateTime publishedAt) {
        this.title = title;
        this.videoId = videoId;
        this.thumbnailUrl = thumbnailUrl;
        this.publishedAt = publishedAt;
    }

    public VideoData(PlaylistItemSnippet playlistItemSnippet) {
        this(
                playlistItemSnippet.getTitle(),
                playlistItemSnippet.getResourceId().getVideoId(),
                playlistItemSnippet.getThumbnails().getStandard().getUrl(),
                playlistItemSnippet.getPublishedAt()
        );
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
