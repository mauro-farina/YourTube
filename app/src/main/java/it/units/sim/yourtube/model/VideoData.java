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
    private final String description;
    private final UserSubscription subscription;

    public VideoData(PlaylistItemSnippet playlistItemSnippet, UserSubscription subscription) {
        this.title = playlistItemSnippet.getTitle();
        this.videoId = playlistItemSnippet.getResourceId().getVideoId();
        this.thumbnailUrl = getHighestResThumbnailUrl(playlistItemSnippet.getThumbnails());
        this.publishedAt = playlistItemSnippet.getPublishedAt();
        this.description = playlistItemSnippet.getDescription();
        this.subscription = subscription;
    }

    protected VideoData(Parcel in) {
        title = in.readString();
        videoId = in.readString();
        thumbnailUrl = in.readString();
        publishedAt = (DateTime) in.readSerializable();
        description = in.readString();
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

    public String getTitle() {
        return title;
    }

    public String getVideoId() {
        return videoId;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public String getDescription() {
        return description;
    }

    public String getReadablePublishedDate() {
        StringBuilder sb = new StringBuilder();
        String dateString = publishedAt.toString().split("T")[0];
        String[] yearMonthDay = dateString.split("-"); // 2023-12-25
        if (yearMonthDay.length != 3) {
            return publishedAt.toString();
        }
        sb.append(monthNumberToString(yearMonthDay[1]));
        sb.append(" ");
        sb.append(yearMonthDay[2]);
        sb.append(", ");
        sb.append(yearMonthDay[0]);
        return sb.toString();
    }

    public UserSubscription getChannel(){
        return subscription;
    }

    public long getPublishedDateInMillis() {
        return publishedAt.getValue();
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
        parcel.writeString(description);
        parcel.writeParcelable(subscription, i);
    }

    private static String monthNumberToString(String monthNumber) {
        switch (monthNumber) {
            case "01" : return "Jan";
            case "02" : return "Feb";
            case "03" : return "Mar";
            case "04" : return "Apr";
            case "05" : return "May";
            case "06" : return "Jun";
            case "07" : return "Jul";
            case "08" : return "Aug";
            case "09" : return "Sep";
            case "10" : return "Oct";
            case "11" : return "Nov";
            case "12" : return "Dec";
            default: return "";
        }
    }
}
