package it.units.sim.yourtube.playlist;

import android.content.Intent;
import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import it.units.sim.yourtube.R;
import it.units.sim.yourtube.model.Playlist;
import it.units.sim.yourtube.model.VideoData;
import it.units.sim.yourtube.utils.NoNavFragment;
import it.units.sim.yourtube.video.VideosAdapter;
import it.units.sim.yourtube.videoplayer.VideoPlayerActivity;

public class PlaylistVideosFragment extends NoNavFragment {

    private Playlist mPlaylist;

    public PlaylistVideosFragment() {
        // Required empty public constructor
    }

    public static PlaylistVideosFragment newInstance(Playlist playlist) {
        PlaylistVideosFragment fragment = new PlaylistVideosFragment();
        Bundle args = new Bundle();
        args.putParcelable("playlist", playlist);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mPlaylist = getArguments().getParcelable("playlist");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_playlist_videos, container, false);
        RecyclerView listView = view.findViewById(R.id.playlist_videos_list);
        VideosAdapter adapter = new VideosAdapter(mPlaylist.getVideos(), v -> {
            VideoData video = (VideoData) v.getTag();
            Bundle extras = new Bundle();
            extras.putParcelable("video", video);
            Intent intent = new Intent(requireActivity(), VideoPlayerActivity.class);
            intent.putExtras(extras);
            startActivity(intent);
        });
        listView.setAdapter(adapter);
        listView.setLayoutManager(new LinearLayoutManager(requireContext()));

        TextView name = view.findViewById(R.id.playlist_videos_name);
        name.setText(mPlaylist.getName());

        return view;
    }

    @Override
    protected String getToolbarTitle() {
        return mPlaylist.getName();
    }

}