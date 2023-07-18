package it.units.sim.yourtube.model;

public class VideoData {
    private final String title;
    private final String videoUrl;
    private final String thumbnailUrl;

    public VideoData(String title, String videoUrl, String thumbnailUrl) {
        this.title = title;
        this.videoUrl = videoUrl;
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }
}
