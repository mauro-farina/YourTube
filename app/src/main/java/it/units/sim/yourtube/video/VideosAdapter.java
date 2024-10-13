package it.units.sim.yourtube.video;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import it.units.sim.yourtube.R;
import it.units.sim.yourtube.model.VideoData;

public class VideosAdapter extends RecyclerView.Adapter<VideosAdapter.ViewHolder> {

    private List<VideoData> videosList;
    private final MutableLiveData<List<VideoData>> selectedVideosLiveData;
    private final View.OnClickListener onVideoClick;
    private final boolean selectable;

    public VideosAdapter(List<VideoData> videosList, View.OnClickListener onVideoClick) {
        this(videosList, onVideoClick, false);
    }

    public VideosAdapter(
            List<VideoData> videosList,
            View.OnClickListener onVideoClick,
            boolean selectable) {
        this.videosList = videosList;
        this.onVideoClick = onVideoClick;
        this.selectable = selectable;
        this.selectedVideosLiveData = new MutableLiveData<>();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setVideosList(List<VideoData> videosList) {
        this.videosList = videosList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VideosAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.list_item_video, parent, false);
        return new VideosAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideosAdapter.ViewHolder holder, int position) {
        VideoData videoData = videosList.get(position);
        holder.itemView.setTag(videosList.get(position));
        holder.itemView.setOnClickListener(onVideoClick);
        if (selectable) {
            holder.itemView.setOnLongClickListener(v -> {
                List<VideoData> selectedVideos = selectedVideosLiveData.getValue();
                selectedVideos = (selectedVideos == null ? new ArrayList<>() : selectedVideos);
                int bgColor = Color.TRANSPARENT;
                if (selectedVideos.contains(videoData)) {
                    selectedVideos.remove(videoData);
                } else {
                    selectedVideos.add(videoData);
                    bgColor = Color.GRAY;
                }
                v.setBackgroundColor(bgColor);
                selectedVideosLiveData.postValue(selectedVideos);
                return true;
            });
        }
        TextView videoTitleTextView = holder.getVideoTitleTextView();
        ImageView thumbnailImageView = holder.getThumbnailImageView();
        ImageView channelImageView = holder.getChannelImageView();
        TextView channelTextView = holder.getChannelTextView();
        videoTitleTextView.setText(videoData.getTitle());
        channelTextView.setText(videoData.getChannel().getChannelName());
        Uri videoThumbnailUri = Uri.parse(videoData.getThumbnailUrl());
        Uri channelThumbnailUri = Uri.parse(videoData.getChannel().getThumbnailUrl());
        thumbnailImageView.setImageURI(videoThumbnailUri);
        channelImageView.setImageURI(channelThumbnailUri);
    }

    public LiveData<List<VideoData>> getSelectedVideosData() {
        return selectedVideosLiveData;
    }

    @Override
    public int getItemCount() {
        return videosList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // will generate a new layout item for each list item
        private final TextView videoTitleTextView;
        private final ImageView thumbnailImageView;
        private final ImageView channelImageView;
        private final TextView channelTextView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            videoTitleTextView = itemView.findViewById(R.id.list_item_video_title);
            thumbnailImageView = itemView.findViewById(R.id.list_item_video_thumbnail);
            channelImageView = itemView.findViewById(R.id.list_item_subscription_thumbnail);
            channelTextView = itemView.findViewById(R.id.list_item_subscription_channel_name);
        }

        public TextView getVideoTitleTextView() {
            return videoTitleTextView;
        }
        public ImageView getThumbnailImageView() {
            return thumbnailImageView;
        }
        public ImageView getChannelImageView() {
            return channelImageView;
        }
        public TextView getChannelTextView() {
            return channelTextView;
        }
    }
}
