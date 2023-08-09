package it.units.sim.yourtube.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.ArrayList;
import java.util.List;

@Entity(tableName = "categories", indices = {@Index(value = {"name"}, unique = true)})
public class Category implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    private int id;
    @NonNull
    @ColumnInfo
    private String name;
    @NonNull
    @ColumnInfo(name = "channels")
    private List<String> channelIds;
    @ColumnInfo(name = "icon_res_id")
    private int drawableIconResId;

    public Category(){
        this("", new ArrayList<>(), 0);
    }

    public Category(@NonNull String name, @NonNull List<String> channelIds, int drawableIconId) {
        this.name = name;
        this.channelIds = channelIds;
        this.drawableIconResId = drawableIconId;
    }

    protected Category(Parcel in) {
        id = in.readInt();
        name = in.readString();
        channelIds = in.createStringArrayList();
        drawableIconResId = in.readInt();
    }

    public static final Creator<Category> CREATOR = new Creator<>() {
        @Override
        public Category createFromParcel(Parcel in) {
            return new Category(in);
        }

        @Override
        public Category[] newArray(int size) {
            return new Category[size];
        }
    };

    public void setId(int id) {
        this.id = id;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    public void setChannelIds(@NonNull List<String> channelIds) {
        this.channelIds = channelIds;
    }

    public void setDrawableIconResId(int drawableIconResId) {
        this.drawableIconResId = drawableIconResId;
    }

    public int getId() {
        return id;
    }

    @NonNull
    public String getName() {
        return name;
    }

    @NonNull
    public List<String> getChannelIds() {
        return channelIds;
    }

    public int getDrawableIconResId() {
        return drawableIconResId;
    }

    @NonNull
    @Override
    public String toString() {
        return name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(name);
        parcel.writeList(channelIds);
        parcel.writeInt(drawableIconResId);
    }
}
