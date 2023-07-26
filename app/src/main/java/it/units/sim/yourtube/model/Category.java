package it.units.sim.yourtube.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.List;

@Entity(tableName = "categories")
public class Category {

//    @PrimaryKey
//    private int uid;
    @PrimaryKey
    public String name;
    @ColumnInfo(name = "channels")
    public List<UserSubscription> channelIds;

}
