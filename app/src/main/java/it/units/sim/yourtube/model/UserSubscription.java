package it.units.sim.yourtube.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.api.client.util.DateTime;
import com.google.api.services.youtube.model.Subscription;
import com.google.api.services.youtube.model.SubscriptionSnippet;

public class UserSubscription implements Parcelable {

    private final String channelName;
    private final String channelId;
    private final String thumbnailUrl;
    private final String uploadsPlaylistId;
    private final DateTime subscribedSince;

    public UserSubscription(Subscription subscription) {
        SubscriptionSnippet snippet = subscription.getSnippet();
        channelName = subscription.getSnippet().getTitle();
        channelId = subscription.getSnippet().getResourceId().getChannelId();
        thumbnailUrl = snippet.getThumbnails().getDefault().getUrl();
        uploadsPlaylistId = "UU" + channelId.substring(2);
        subscribedSince = snippet.getPublishedAt();
    }

    protected UserSubscription(Parcel in) {
        channelName = in.readString();
        channelId = in.readString();
        thumbnailUrl = in.readString();
        uploadsPlaylistId = in.readString();
        subscribedSince = (DateTime) in.readSerializable();
    }

    public static final Creator<UserSubscription> CREATOR = new Creator<>() {
        @Override
        public UserSubscription createFromParcel(Parcel in) {
            return new UserSubscription(in);
        }

        @Override
        public UserSubscription[] newArray(int size) {
            return new UserSubscription[size];
        }
    };

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

    public DateTime getSubscribedSince() {
        return subscribedSince;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof UserSubscription) {
            return ((UserSubscription) obj).getChannelId().equals(this.getChannelId());
        } else {
            return false;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(channelName);
        parcel.writeString(channelId);
        parcel.writeString(thumbnailUrl);
        parcel.writeString(uploadsPlaylistId);
        parcel.writeSerializable(subscribedSince);
    }
}
