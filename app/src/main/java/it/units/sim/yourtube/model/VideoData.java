package it.units.sim.yourtube.model;

import com.google.api.client.util.DateTime;
import com.google.api.services.youtube.model.PlaylistItemSnippet;
import com.google.api.services.youtube.model.ThumbnailDetails;

public class VideoData {
    private final String title;
    private final String videoId;
    private final String thumbnailUrl;
    private final DateTime publishedAt;
    private final UserSubscription subscription;

    public VideoData(PlaylistItemSnippet playlistItemSnippet, UserSubscription subscription) {
        this.title = playlistItemSnippet.getTitle();
        this.videoId = playlistItemSnippet.getResourceId().getVideoId();
        // might be missing...
        this.thumbnailUrl = getHighestResThumbnailUrl(playlistItemSnippet.getThumbnails());
        this.publishedAt = playlistItemSnippet.getPublishedAt();
        this.subscription = subscription;
    }

    private String getHighestResThumbnailUrl(ThumbnailDetails thumbnails) {
        String url = "";
        if (thumbnails.getMaxres() != null)
            url = thumbnails.getMaxres().getUrl();
        if (thumbnails.getStandard() != null)
            url = thumbnails.getStandard().getUrl();
        if (thumbnails.getHigh() != null)
            url = thumbnails.getHigh().getUrl();
        if (thumbnails.getMedium() != null)
            url = thumbnails.getMedium().getUrl();
        if (thumbnails.getDefault() != null)
            url = thumbnails.getDefault().getUrl();
        return url;
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
