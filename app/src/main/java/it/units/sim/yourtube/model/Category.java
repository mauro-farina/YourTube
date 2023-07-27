package it.units.sim.yourtube.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.ArrayList;
import java.util.List;

@Entity(tableName = "categories")
public class Category {

    @PrimaryKey
    @NonNull
    public String name;
    @ColumnInfo(name = "channels")
    public List<String> channelIds;

    public Category(){
        this("", new ArrayList<>());
    }

    public Category(@NonNull String name, List<String> channelIds) {
        this.name = name;
        this.channelIds = channelIds;
    }

    public Category(String name) {
        this(name, new ArrayList<>());
    }

    @NonNull
    @Override
    public String toString() {
        return name;
    }

}
