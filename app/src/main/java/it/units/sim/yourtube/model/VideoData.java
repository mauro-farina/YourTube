package it.units.sim.yourtube.model;

import com.google.api.services.youtube.model.PlaylistItem;

public class VideoData {
    private final String title;
    private final String videoId;
    private final String thumbnailUrl;

    public VideoData(String title, String videoId, String thumbnailUrl) {
        this.title = title;
        this.videoId = videoId;
        this.thumbnailUrl = thumbnailUrl;
    }

    public VideoData(PlaylistItem playlistItem) {
        this(
                playlistItem.getSnippet().getTitle(),
                playlistItem.getSnippet().getResourceId().getVideoId(),
                playlistItem.getSnippet().getThumbnails().getStandard().getUrl()
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
}
