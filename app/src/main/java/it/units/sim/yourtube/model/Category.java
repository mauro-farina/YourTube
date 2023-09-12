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
    @NonNull
    @ColumnInfo(name = "icon_res_id")
    private CategoryIcon categoryIcon;
    @NonNull
    @ColumnInfo(name = "owner")
    private String ownerEmail;

    public Category(){
        this("", new ArrayList<>(), CategoryIcon.ICON_CATEGORY_TRAVEL, "");
    }

    public Category(@NonNull String name, @NonNull List<String> channelIds, @NonNull CategoryIcon categoryIcon, @NonNull String ownerEmail) {
        this.name = name;
        this.channelIds = channelIds;
        this.categoryIcon = categoryIcon;
        this.ownerEmail = ownerEmail;
    }

    protected Category(Parcel in) {
        id = in.readInt();
        name = in.readString();
        channelIds = in.createStringArrayList();
        categoryIcon = (CategoryIcon) in.readSerializable();
        ownerEmail = in.readString();
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

    public void setCategoryIcon(@NonNull CategoryIcon newDrawableIconId) {
        this.categoryIcon = newDrawableIconId;
    }

    public void setOwnerEmail(@NonNull String ownerEmail) {
        this.ownerEmail = ownerEmail;
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

    @NonNull
    public CategoryIcon getCategoryIcon() {
        return categoryIcon;
    }

    @NonNull
    public String getOwnerEmail() {
        return ownerEmail;
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
        parcel.writeSerializable(categoryIcon);
        parcel.writeString(ownerEmail);
    }
}
