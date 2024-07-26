package it.units.sim.yourtube.subscription;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import it.units.sim.yourtube.R;
import it.units.sim.yourtube.YouTubeDataViewModel;
import it.units.sim.yourtube.model.UserSubscription;
import it.units.sim.yourtube.model.VideoData;
import it.units.sim.yourtube.utils.EmptyMenuProvider;
import it.units.sim.yourtube.video.VideosAdapter;
import it.units.sim.yourtube.videoplayer.VideoPlayerActivity;


public class ChannelVideosFragment extends Fragment {


    private YouTubeDataViewModel viewModel;
    private VideosAdapter adapter;
    private UserSubscription channel;
    private ActionBar toolbar;
    private EmptyMenuProvider menuProvider;

    public ChannelVideosFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() == null) {
            // TODO: Error message
            return;
        }

        ViewModelProvider.AndroidViewModelFactory factory =
                ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().getApplication());
        viewModel = new ViewModelProvider(requireActivity(), factory).get(YouTubeDataViewModel.class);

        channel = getArguments().getParcelable("channel");
        viewModel.fetchVideos(channel);

        menuProvider = new EmptyMenuProvider();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_channel_videos, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.channel_videos_recycler_view);

        adapter = new VideosAdapter(new ArrayList<>(), clickedView -> {
            VideoData video = (VideoData) clickedView.getTag();
            Bundle extras = new Bundle();
            extras.putParcelable("video", video);
            Intent intent = new Intent(requireActivity(), VideoPlayerActivity.class);
            intent.putExtras(extras);
            startActivity(intent);
        });

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setItemViewCacheSize(30);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel.getChannelVideos().observe(getViewLifecycleOwner(), list -> adapter.setVideosList(list));
    }

    @Override
    public void onResume() {
        super.onResume();
        toggleBottomNav();
        toolbar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
        if (toolbar != null) {
            toolbar.setDisplayHomeAsUpEnabled(true);
            toolbar.setTitle(channel.getChannelName());
            requireActivity().addMenuProvider(menuProvider);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        toggleBottomNav();
        requireActivity().removeMenuProvider(menuProvider);
        if (toolbar != null) {
            toolbar.setDisplayHomeAsUpEnabled(false);
            toolbar.setTitle(R.string.app_name);
        }
    }

    private void toggleBottomNav() {
        View bottomNav = requireActivity().findViewById(R.id.bottom_navigation);
        if (bottomNav.getVisibility() == View.VISIBLE) {
            bottomNav.setVisibility(View.GONE);
        } else {
            bottomNav.setVisibility(View.VISIBLE);
        }
    }

}