package it.units.sim.yourtube.data;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tencent.mmkv.MMKV;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import it.units.sim.yourtube.model.VideoData;

public class WatchLaterDatabase {

    private final String owner;
    private final MMKV db;
    private final Gson gson = new Gson();
    private final TypeToken<List<VideoData>> listType =
            new TypeToken<List<VideoData>>() {};

    public WatchLaterDatabase(String owner) {
        this.owner = owner;
        this.db = MMKV.defaultMMKV();
    }

    public List<VideoData> getVideos() {
        String json = db.getString(owner, null);
        if (json == null)
            return new ArrayList<>();
//        VideoData[] arr = gson.fromJson(json, String[].class);
//        return new ArrayList<>(List.of(arr));
        return gson.fromJson(json, listType.getType());
    }

    private void setVideos(List<VideoData> videos) {
        String json = gson.toJson(videos, listType.getType());
        db.putString(owner, json);
    }

    public void addVideo(VideoData video) {
        List<VideoData> list = getVideos();
        boolean exists = false;
        for (VideoData vd : list) {
            if (vd.getVideoId().equals(video.getVideoId())) { exists = true; break; }
        }
        if (!exists) {
            list.add(video);
            setVideos(list);
        }
//        if (!items.contains(video)) {
//            items.add(video);
//            setVideos(items);
//        }
    }

    public void removeVideo(VideoData video) {
//        List<VideoData> items = getVideos();
//        if (items.remove(video)) {
//            setVideos(items);
//        }
        String videoId = video.getVideoId();
        List<VideoData> list = getVideos();
        Iterator<VideoData> it = list.iterator();
        while (it.hasNext()) {
            if (it.next().getVideoId().equals(videoId)) {
                it.remove();
                setVideos(list);
                break;
            }
        }
    }
}
