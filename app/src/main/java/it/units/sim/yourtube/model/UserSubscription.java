package it.units.sim.yourtube.model;

import androidx.annotation.NonNull;

import com.google.api.services.youtube.model.Subscription;

public class UserSubscription {

    private final String channelName;
    private final String channelId;
    private final String thumbnailUrl;
    private String uploadsPlaylistId; // Uri?

    public UserSubscription(String channelName, String channelId, String thumbnailUrl) {
        this.channelName = channelName;
        this.channelId = channelId;
        this.thumbnailUrl = thumbnailUrl;
    }

    public UserSubscription(Subscription subscription) {
        this(
                subscription.getSnippet().getTitle(),
                subscription.getSnippet().getResourceId().getChannelId(),
                subscription.getSnippet().getThumbnails().getDefault().getUrl()
        );
    }

    @NonNull
    @Override
    public String toString() {
        return "@" + channelName
                + System.lineSeparator()
                + "ID: " + channelId
                + System.lineSeparator()
                + "Thumbnail: " + thumbnailUrl;
    }

    public String getUploadsPlaylistId() {
        return uploadsPlaylistId;
    }

    public void setUploadsPlaylistId(String uploadsPlaylistId) {
        this.uploadsPlaylistId = uploadsPlaylistId;
    }

    public String getChannelName() {
        return channelName;
    }

    public String getChannelId() {
        return channelId;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }
}
