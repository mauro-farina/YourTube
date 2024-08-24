package it.units.sim.yourtube.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.List;

@Entity(tableName = "playlist")
public class Playlist implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo
    private final String name;
    @ColumnInfo
    private final String owner;
    @ColumnInfo
    private final List<VideoData> videos;

    public Playlist(@NonNull String name, @NonNull String owner, @NonNull List<VideoData> videos) {
        this.name = name;
        this.owner = owner;
        this.videos = videos;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getOwner() {
        return owner;
    }

    public List<VideoData> getVideos() {
        return videos;
    }

    public void addVideo(VideoData video) {
        videos.add(video);
    }

}
