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
    public int id;

    @NonNull
    @ColumnInfo
    public String name;
    @NonNull
    @ColumnInfo(name = "channels")
    public List<String> channelIds;
    @ColumnInfo(name = "icon_res_id")
    public int drawableIconResId;

    public Category(){
        this("", new ArrayList<>(), 0);
    }

    public Category(@NonNull String name, @NonNull List<String> channelIds, int drawableIconId) {
        this.name = name;
        this.channelIds = channelIds;
        this.drawableIconResId = drawableIconId;
    }

    @NonNull
    @Override
    public String toString() {
        return name;
    }

}
