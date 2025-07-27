package it.units.sim.yourtube.model;

//import androidx.annotation.NonNull;
//import androidx.room.ColumnInfo;
//import androidx.room.Entity;
//import androidx.room.Index;
//import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.List;

//@Entity(tableName = "watchlater", indices = {@Index(value = {"owner"}, unique = true)})
public class WatchLaterPlaylist implements Serializable {

//    @PrimaryKey
//    @NonNull
//    @ColumnInfo(name = "owner")
    private String ownerEmail;

//    @NonNull
//    @ColumnInfo(name = "videos")
    private List<String> videoIds;

    public WatchLaterPlaylist(String ownerEmail, List<String> videoIds) {
        this.ownerEmail = ownerEmail;
        this.videoIds = videoIds;
    }

    public void setOwnerEmail(String ownerEmail) {
        this.ownerEmail = ownerEmail;
    }

    public void setVideoIds(List<String> videoIds) {
        this.videoIds = videoIds;
    }

//    @NonNull
    public List<String> getVideoIds() {
        return videoIds;
    }

    public String getOwnerEmail() {
        return ownerEmail;
    }
}
