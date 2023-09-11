package it.units.sim.yourtube.video;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.Comparator;
import java.util.List;

import it.units.sim.yourtube.R;
import it.units.sim.yourtube.model.VideoData;

public class VideosAdapter extends RecyclerView.Adapter<VideosAdapter.ViewHolder> {

    private List<VideoData> videosList;
    private final View.OnClickListener onVideoClick;

    public VideosAdapter(List<VideoData> videosList, View.OnClickListener onVideoClick) {
        this.videosList = videosList;
        this.onVideoClick = onVideoClick;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setVideosList(List<VideoData> videosList) {
        videosList.sort(Comparator.comparing(VideoData::getPublishedDateInMillis).reversed());
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
        holder.itemView.setTag(videosList.get(position));
        holder.itemView.setOnClickListener(onVideoClick);
        TextView videoTitleTextView = holder.getVideoTitleTextView();
        ImageView thumbnailImageView = holder.getThumbnailImageView();
        ImageView channelImageView = holder.getChannelImageView();
        TextView channelTextView = holder.getChannelTextView();
        videoTitleTextView.setText(videosList.get(position).getTitle());
        channelTextView.setText(videosList.get(position).getChannel().getChannelName());
        Picasso
                .get()
                .load(videosList.get(position).getThumbnailUrl())
                .into(thumbnailImageView);
        Picasso
                .get()
                .load(videosList.get(position).getChannel().getThumbnailUrl())
                .into(channelImageView);
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
