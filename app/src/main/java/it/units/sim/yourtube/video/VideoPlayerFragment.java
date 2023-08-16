package it.units.sim.yourtube.video;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.FullscreenListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;
import com.squareup.picasso.Picasso;

import it.units.sim.yourtube.R;
import it.units.sim.yourtube.model.VideoData;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;

public class VideoPlayerFragment extends Fragment {

    private ActionBar toolbar;
    private Window window;
    private int originalSystemUiVisibility;
    private YouTubePlayerView youTubePlayerView;
    private YouTubePlayer youTubePlayerWhenReady;
    private VideoData video;
    private boolean isFullscreen;
    private final Handler handler = new Handler();

    public VideoPlayerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        window = requireActivity().getWindow();
        originalSystemUiVisibility = window.getDecorView().getSystemUiVisibility();
        toolbar = ((AppCompatActivity) requireActivity()).getSupportActionBar();

        if (getArguments() != null) {
            video = getArguments().getParcelable("video");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        toggleBottomNavVisibility();
        if (toolbar != null) {
            toolbar.setDisplayHomeAsUpEnabled(true);
            toolbar.setTitle(video.getReadablePublishedDate());
        }
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    public void onStop() {
        super.onStop();
        toggleBottomNavVisibility();
        if (toolbar != null) {
            toolbar.setDisplayHomeAsUpEnabled(false);
            toolbar.setTitle(R.string.app_name);
        }
        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        turnImmersionModeOff();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE
                && !isFullscreen
                && youTubePlayerWhenReady != null) {
            youTubePlayerWhenReady.toggleFullscreen();
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT
                && isFullscreen
                && youTubePlayerWhenReady != null) {
            youTubePlayerWhenReady.toggleFullscreen();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video_player, container, false);

        youTubePlayerView = view.findViewById(R.id.youtube_player_view);
        FrameLayout fullscreenViewContainer = view.findViewById(R.id.youtube_player_fullscreen_container);
        getLifecycle().addObserver(youTubePlayerView);

        YouTubePlayerListener playerListener = new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                youTubePlayerWhenReady = youTubePlayer;
                youTubePlayer.loadVideo(video.getVideoId(), 0);
                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    youTubePlayer.toggleFullscreen();
                }
            }
        };

        IFramePlayerOptions playerOptions = new IFramePlayerOptions.Builder()
                .controls(1)
                .fullscreen(1)
                .build();

        youTubePlayerView.addFullscreenListener(new FullscreenListener() {
            @Override
            public void onEnterFullscreen(@NonNull View fullscreenView, @NonNull Function0<Unit> exitFullscreen) {
                isFullscreen = true;
                youTubePlayerView.setVisibility(View.GONE);
                fullscreenViewContainer.setVisibility(View.VISIBLE);
                fullscreenViewContainer.addView(fullscreenView);
                toggleToolbarVisibility();
                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                    requireActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    handler.postDelayed(
                            () -> requireActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED),
                            3*1000
                    );
                }
                turnImmersionModeOn();
            }

            @SuppressLint("SourceLockedOrientationActivity")
            @Override
            public void onExitFullscreen() {
                isFullscreen = false;
                youTubePlayerView.setVisibility(View.VISIBLE);
                fullscreenViewContainer.setVisibility(View.GONE);
                fullscreenViewContainer.removeAllViews();
                toggleToolbarVisibility();
                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    requireActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    handler.postDelayed(
                            () -> requireActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED),
                            3*1000
                    );
                }
                turnImmersionModeOff();
            }
        });

        OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (isFullscreen) {
                    youTubePlayerWhenReady.toggleFullscreen();
                } else {
                    NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
                    navController.popBackStack();
                }
            }
        };

        youTubePlayerView.initialize(
                playerListener,
                playerOptions
        );

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), onBackPressedCallback);
        initVideoInfoUi(view);

        return view;
    }

    private void initVideoInfoUi(View view) {
        ViewModelProvider.AndroidViewModelFactory factory =
                ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().getApplication());
        VideoPlayerViewModel viewModel = new ViewModelProvider(requireActivity(), factory).get(VideoPlayerViewModel.class);
        viewModel.setVideoId(video.getVideoId());

        TextView videoTitle = view.findViewById(R.id.video_player_title);
        TextView videoViewsCount = view.findViewById(R.id.video_player_views_counter);
//        TextView videoDate = view.findViewById(R.id.video_player_date);
        TextView videoChannelName = view.findViewById(R.id.list_item_subscription_channel_name);
        ImageView videoChannelThumbnail = view.findViewById(R.id.list_item_subscription_thumbnail);
        TextView videoDescription = view.findViewById(R.id.video_player_description);
        TextView videoLikesCounter = view.findViewById(R.id.video_player_likes_counter);

        videoTitle.setText(video.getTitle());
//        videoDate.setText(video.getReadablePublishedDate());
        videoChannelName.setText(video.getChannel().getChannelName());
        videoDescription.setText((video.getDescription()));
        viewModel.getViewsCount().observe(getViewLifecycleOwner(), views -> videoViewsCount.setText(views + " Views"));
        viewModel.getLikesCount().observe(getViewLifecycleOwner(), likes -> videoLikesCounter.setText(likes + " Likes"));
        Picasso
                .get()
                .load(video.getChannel().getThumbnailUrl())
                .into(videoChannelThumbnail);
    }

    private void toggleBottomNavVisibility() {
        View bottomNav = requireActivity().findViewById(R.id.bottom_navigation);
        if (bottomNav.getVisibility() == View.VISIBLE) {
            bottomNav.setVisibility(View.GONE);
        } else {
            bottomNav.setVisibility(View.VISIBLE);
        }
    }

    private void toggleToolbarVisibility() {
        Toolbar toolbar = requireActivity().findViewById(R.id.top_app_bar);
        if (toolbar.getVisibility() == View.VISIBLE) {
            toolbar.setVisibility(View.GONE);
        } else {
            toolbar.setVisibility(View.VISIBLE);
        }
    }

    private void turnImmersionModeOn() {
        int immersiveModeFlags = View.SYSTEM_UI_FLAG_IMMERSIVE
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN;
        window.getDecorView().setSystemUiVisibility(immersiveModeFlags);
    }

    private void turnImmersionModeOff() {
        window.getDecorView().setSystemUiVisibility(originalSystemUiVisibility);
    }

}