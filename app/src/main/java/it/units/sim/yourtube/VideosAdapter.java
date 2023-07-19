package it.units.sim.yourtube;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import it.units.sim.yourtube.model.VideoData;

public class VideosAdapter extends RecyclerView.Adapter<VideosAdapter.ViewHolder> {

    private List<VideoData> videosList;

    public VideosAdapter(List<VideoData> videosList) {
        this.videosList = videosList;
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
        TextView channelNameTextView = holder.getVideoTitleTextView();
        ImageView thumbnailImageView = holder.getThumbnailImageView();
        channelNameTextView.setText(videosList.get(position).getTitle());
        Picasso
                .get()
                .load(videosList.get(position).getThumbnailUrl())
                .into(thumbnailImageView);
    }

    @Override
    public int getItemCount() {
        return videosList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // will generate a new layout item for each list item
        private final TextView videoTitleTextView;
        private final ImageView thumbnailImageView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            videoTitleTextView = itemView.findViewById(R.id.list_item_video_title);
            thumbnailImageView = itemView.findViewById(R.id.list_item_video_thumbnail);
        }

        public TextView getVideoTitleTextView() {
            return videoTitleTextView;
        }
        public ImageView getThumbnailImageView() {
            return thumbnailImageView;
        }

    }
}
