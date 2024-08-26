package it.units.sim.yourtube.playlist;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import it.units.sim.yourtube.R;
import it.units.sim.yourtube.model.Playlist;
import it.units.sim.yourtube.model.VideoData;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.ViewHolder> {

    private List<Playlist> playlists;
    private final View.OnLongClickListener onLongClickListener;

    public PlaylistAdapter(List<Playlist> playlists,
                           View.OnLongClickListener onLongClickListener) {
        this.playlists = playlists;
        this.onLongClickListener = onLongClickListener;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setList(List<Playlist> playlists) {
        this.playlists = playlists;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.list_item_playlist, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistAdapter.ViewHolder holder, int position) {
        Playlist playlist = playlists.get(position);
        holder.name.getRootView().setOnLongClickListener(onLongClickListener);
        holder.name.getRootView().setTag(playlist);
        holder.name.setText(playlist.getName());
        int numVideos = playlist.getVideos().size();
        if (numVideos > 0) {
            String firstVideoThumbnailUrl = playlist.getVideos().get(0).getThumbnailUrl();
            holder.thumbnail.setImageURI(Uri.parse(firstVideoThumbnailUrl));
            holder.size.setText(numVideos + " Videos");
        } else {
            holder.size.setText("Empty");
        }
    }

    @Override
    public int getItemCount() {
        return playlists.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView name;
        private final ImageView thumbnail;
        private final TextView size;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.list_item_playlist_name);
            thumbnail = itemView.findViewById(R.id.list_item_playlist_thumbnail);
            size = itemView.findViewById(R.id.list_item_playlist_num_videos);
        }

    }

}
