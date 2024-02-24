package it.units.sim.yourtube.videoplayer;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.squareup.picasso.Picasso;

import it.units.sim.yourtube.R;
import it.units.sim.yourtube.model.VideoData;

public class VideoInfosFragment extends Fragment {

    private static final String ARG = "video";

    private VideoData video;
    private VideoPlayerViewModel viewModel;

    public VideoInfosFragment() {
        // Required empty public constructor
    }

    public static VideoInfosFragment newInstance(VideoData video) {
        VideoInfosFragment fragment = new VideoInfosFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG, video);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            video = getArguments().getParcelable(ARG);
        }

        ViewModelProvider.AndroidViewModelFactory factory =
                ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().getApplication());
        viewModel = new ViewModelProvider(requireActivity(), factory).get(VideoPlayerViewModel.class);
        viewModel.setVideoId(video.getVideoId());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video_infos, container, false);
        TextView videoTitle = view.findViewById(R.id.video_player_title);
        TextView videoViewsCount = view.findViewById(R.id.video_player_views_counter);
        TextView videoChannelName = view.findViewById(R.id.list_item_subscription_channel_name);
        ImageView videoChannelThumbnail = view.findViewById(R.id.list_item_subscription_thumbnail);
        ScrollView descriptionScrollview = view.findViewById(R.id.video_player_description_scroll_view);
        TextView videoDescription = view.findViewById(R.id.video_player_description);
        TextView videoLikesCounter = view.findViewById(R.id.video_player_likes_counter);

        videoTitle.setText(video.getTitle());
        videoChannelName.setText(video.getChannel().getChannelName());
        videoDescription.setText((video.getDescription()));
        viewModel.getViewsCount().observe(getViewLifecycleOwner(), views -> videoViewsCount.setText(getString(R.string.number_views, views)));
        viewModel.getLikesCount().observe(getViewLifecycleOwner(), videoLikesCounter::setText);
        Picasso
                .get()
                .load(video.getChannel().getThumbnailUrl())
                .into(videoChannelThumbnail);

        RecyclerView commentsRecyclerView = view.findViewById(R.id.video_player_comments_recycler_view);
        CommentsAdapter adapter = new CommentsAdapter(viewModel.getComments().getValue());
        commentsRecyclerView.setAdapter(adapter);
        commentsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        viewModel.getComments().observe(getViewLifecycleOwner(), adapter::setCommentsList);

        TabLayout tabLayout = view.findViewById(R.id.video_player_tab_layout);
        tabLayout.addOnTabSelectedListener(
                new TabLayout.OnTabSelectedListener() {
                    @Override
                    public void onTabSelected(TabLayout.Tab tab) {
                        if (tab.equals(tabLayout.getTabAt(0)))      // Description
                            descriptionScrollview.setVisibility(View.VISIBLE);
                        else                                             // Comments
                            commentsRecyclerView.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onTabUnselected(TabLayout.Tab tab) {
                        if (tab.equals(tabLayout.getTabAt(0)))      // Description
                            descriptionScrollview.setVisibility(View.GONE);
                        else                                             // Comments
                            commentsRecyclerView.setVisibility(View.GONE);
                    }

                    @Override
                    public void onTabReselected(TabLayout.Tab tab) {
                        // nothing
                    }
                }
        );
        return view;
    }
}