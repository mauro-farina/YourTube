package it.units.sim.yourtube.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.ArrayList;
import java.util.List;

@Entity(tableName = "playlist")
public class Playlist implements Parcelable {

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

    protected Playlist(Parcel in) {
        id = in.readInt();
        name = in.readString();
        owner = in.readString();
        videos = in.createTypedArrayList(VideoData.CREATOR);
    }

    public static final Creator<Playlist> CREATOR = new Creator<>() {
        @Override
        public Playlist createFromParcel(Parcel in) {
            return new Playlist(in);
        }

        @Override
        public Playlist[] newArray(int size) {
            return new Playlist[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(name);
        parcel.writeString(owner);
        parcel.writeList(videos);
    }
}
