package it.units.sim.yourtube.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity(tableName = "playlist")
public class Playlist implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo
    private String name;
    @ColumnInfo
    private String owner;
    @ColumnInfo
    private List<VideoData> videos;

    public Playlist(@NonNull String name, @NonNull String owner, List<VideoData> videos) {
        this.name = name;
        this.owner = owner;
        if (videos == null)
            this.videos = new ArrayList<>();
        else
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

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public void setVideos(List<VideoData> videos) {
        this.videos = videos;
    }

    public void addVideo(VideoData video) {
        videos.add(video);
    }

}
