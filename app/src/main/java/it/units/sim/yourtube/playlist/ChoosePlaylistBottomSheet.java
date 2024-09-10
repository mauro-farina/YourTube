package it.units.sim.yourtube.playlist;

import android.os.Bundle;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;

import it.units.sim.yourtube.R;
import it.units.sim.yourtube.data.PlaylistViewModel;
import it.units.sim.yourtube.model.Playlist;
import it.units.sim.yourtube.model.VideoData;

public class ChoosePlaylistBottomSheet extends BottomSheetDialogFragment {

    public static final String VIDEO_ARG = "video";
    private VideoData mVideoData;

    public ChoosePlaylistBottomSheet() {
        // Empty constructor
    }

    public static ChoosePlaylistBottomSheet newInstance(VideoData video) {
        ChoosePlaylistBottomSheet fragment = new ChoosePlaylistBottomSheet();
        Bundle args = new Bundle();
        args.putParcelable(VIDEO_ARG, video);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mVideoData = getArguments().getParcelable(VIDEO_ARG);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottomsheet_choose_playlist, container, false);
        PlaylistViewModel viewModel = new ViewModelProvider(requireActivity()).get(PlaylistViewModel.class);

        RecyclerView recyclerView = view.findViewById(R.id.playlist_list);
        PlaylistAdapter adapter = new PlaylistAdapter(new ArrayList<>(),
                v -> {
                    Playlist playlist = (Playlist) v.getTag();
                    viewModel.addToPlaylist(playlist, mVideoData);
                },
                v -> true);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);
        viewModel.getPlaylists().observe(getViewLifecycleOwner(), adapter::setList);
        return view;
    }

}