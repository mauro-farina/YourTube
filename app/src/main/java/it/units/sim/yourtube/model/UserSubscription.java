package it.units.sim.yourtube.model;

import androidx.annotation.NonNull;

import com.google.api.services.youtube.model.Subscription;
import com.google.api.services.youtube.model.SubscriptionSnippet;

public class UserSubscription {

    private final String channelName;
    private final String channelId;
    private final String thumbnailUrl;
    private final String uploadsPlaylistId;

    public UserSubscription(Subscription subscription) {
        SubscriptionSnippet snippet = subscription.getSnippet();
        channelName = subscription.getSnippet().getTitle();
        channelId = subscription.getSnippet().getResourceId().getChannelId();
        thumbnailUrl = snippet.getThumbnails().getDefault().getUrl();
        uploadsPlaylistId = "UU" + channelId.substring(2);
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
