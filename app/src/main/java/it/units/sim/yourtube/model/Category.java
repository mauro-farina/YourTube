package it.units.sim.yourtube.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.ArrayList;
import java.util.List;

@Entity(tableName = "categories", indices = {@Index(value = {"name"}, unique = true)})
public class Category {

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

}
