package it.units.sim.yourtube.videoplayer;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import it.units.sim.yourtube.R;
import it.units.sim.yourtube.model.VideoData;
import it.units.sim.yourtube.playlist.ChoosePlaylistBottomSheet;
import it.units.sim.yourtube.utils.DateFormatter;

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
        TextView videoViewsCount = view.findViewById(R.id.video_player_views);
        TextView videoChannelName = view.findViewById(R.id.list_item_subscription_channel_name);
        ImageView videoChannelThumbnail = view.findViewById(R.id.list_item_subscription_thumbnail);
        TextView videoDescription = view.findViewById(R.id.video_player_description);
        TextView videoLikesCounter = view.findViewById(R.id.video_player_likes);
        TextView publishedDate = view.findViewById(R.id.video_player_date);

//        import android.text.util.Linkify;
//        Linkify.addLinks(videoDescription, Linkify.WEB_URLS);

        videoTitle.setText(video.getTitle());
        String date = DateFormatter.formatDate(video.getPublishedDateInMillis(), getResources());
        publishedDate.setText(date);
        videoChannelName.setText(video.getChannel().getChannelName());
        if (video.getDescription().length() > 0)
            videoDescription.setText(getString(R.string.video_description_template, video.getDescription()));

        viewModel.getViewsCount().observe(getViewLifecycleOwner(), videoViewsCount::setText);
        viewModel.getLikesCount().observe(getViewLifecycleOwner(), videoLikesCounter::setText);
        Uri channelThumbnailUri = Uri.parse(video.getChannel().getThumbnailUrl());
        videoChannelThumbnail.setImageURI(channelThumbnailUri);

        RecyclerView commentsRecyclerView = view.findViewById(R.id.video_player_comments_recycler_view);
        CommentsAdapter adapter = new CommentsAdapter(viewModel.getComments().getValue());
        commentsRecyclerView.setAdapter(adapter);
        commentsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        viewModel.getComments().observe(getViewLifecycleOwner(), adapter::setCommentsList);

        view.findViewById(R.id.video_player_container_title_description).setOnClickListener(v -> {
            VideoInfoBottomSheet bottomSheet = VideoInfoBottomSheet.newInstance(video);
            bottomSheet.show(getChildFragmentManager(), bottomSheet.getTag());
        });

        view.findViewById(R.id.video_player_share).setOnClickListener(v -> {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            String shareBody = "https://youtu.be/" + video.getVideoId();
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
            startActivity(Intent.createChooser(shareIntent, "Share via"));
        });

        view.findViewById(R.id.video_player_add_to_playlist).setOnClickListener(v -> {
            ChoosePlaylistBottomSheet bottomSheet = ChoosePlaylistBottomSheet.newInstance(video);
            bottomSheet.show(getChildFragmentManager(), bottomSheet.getTag());
        });

        return view;
    }
}