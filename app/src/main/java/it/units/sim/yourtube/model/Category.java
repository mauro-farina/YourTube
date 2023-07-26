package it.units.sim.yourtube.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.List;

@Entity(tableName = "categories")
public class Category {

    @PrimaryKey
    @NonNull
    public String name;
    @ColumnInfo(name = "channels")
    public List<String> channelIds;

}
