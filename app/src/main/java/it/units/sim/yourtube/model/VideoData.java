package it.units.sim.yourtube.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.api.client.util.DateTime;
import com.google.api.services.youtube.model.PlaylistItemSnippet;
import com.google.api.services.youtube.model.ThumbnailDetails;

public class VideoData implements Parcelable {
    private final String title;
    private final String videoId;
    private final String thumbnailUrl;
    private final DateTime publishedAt;
    private final UserSubscription subscription;

    public VideoData(PlaylistItemSnippet playlistItemSnippet, UserSubscription subscription) {
        this.title = playlistItemSnippet.getTitle();
        this.videoId = playlistItemSnippet.getResourceId().getVideoId();
        this.thumbnailUrl = getHighestResThumbnailUrl(playlistItemSnippet.getThumbnails());
        this.publishedAt = playlistItemSnippet.getPublishedAt();
        this.subscription = subscription;
    }

    protected VideoData(Parcel in) {
        title = in.readString();
        videoId = in.readString();
        thumbnailUrl = in.readString();
        publishedAt = (DateTime) in.readSerializable();
        subscription = in.readParcelable(UserSubscription.class.getClassLoader());
    }

    public static final Creator<VideoData> CREATOR = new Creator<>() {
        @Override
        public VideoData createFromParcel(Parcel in) {
            return new VideoData(in);
        }

        @Override
        public VideoData[] newArray(int size) {
            return new VideoData[size];
        }
    };

    private String getHighestResThumbnailUrl(ThumbnailDetails thumbnails) {
        String url = "";
        if (thumbnails.getDefault() != null)
            url = thumbnails.getDefault().getUrl();
        if (thumbnails.getMedium() != null)
            url = thumbnails.getMedium().getUrl();
        if (thumbnails.getHigh() != null)
            url = thumbnails.getHigh().getUrl();
        if (thumbnails.getStandard() != null)
            url = thumbnails.getStandard().getUrl();
        if (thumbnails.getMaxres() != null)
            url = thumbnails.getMaxres().getUrl();
        return url;
    }

    @NonNull
    @Override
    public String toString() {
        return title + " by " + getChannel().getChannelName();
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(title);
        parcel.writeString(videoId);
        parcel.writeString(thumbnailUrl);
        parcel.writeSerializable(publishedAt);
        parcel.writeParcelable(subscription, i);
    }
}
