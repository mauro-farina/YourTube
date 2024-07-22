package it.units.sim.yourtube.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "watchdata")
public class WatchData {

    @PrimaryKey(autoGenerate = true)
    private int id;
    @NonNull
    @ColumnInfo(name = "video_id")
    private String videoId;
    @ColumnInfo(name = "timestamp")
    private float timestamp;
    @ColumnInfo(name = "watched")
    private boolean watched;
    @NonNull
    @ColumnInfo(name = "owner")
    private String ownerEmail;

    @Ignore
    public WatchData() {
        this("", 0, false, "");
    }

    public WatchData(@NonNull String videoId, float timestamp, boolean watched, @NonNull String ownerEmail) {
        this.videoId = videoId;
        this.timestamp = timestamp;
        this.watched = watched;
        this.ownerEmail = ownerEmail;
    }

    @NonNull
    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(@NonNull String videoId) {
        this.videoId = videoId;
    }

    public float getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(float timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isWatched() {
        return watched;
    }

    public void setWatched(boolean watched) {
        this.watched = watched;
    }

    @NonNull
    public String getOwnerEmail() {
        return ownerEmail;
    }

    public void setOwnerEmail(@NonNull String ownerEmail) {
        this.ownerEmail = ownerEmail;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
