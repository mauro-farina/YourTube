package it.units.sim.yourtube.videoplayer;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.FullscreenListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import it.units.sim.yourtube.R;
import it.units.sim.yourtube.model.VideoData;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;

public class VideoPlayerFragment extends Fragment {

    private Window window;
    private int originalSystemUiVisibility;
    private YouTubePlayerView youTubePlayerView;
    private YouTubePlayer youTubePlayerWhenReady;
    private VideoData video;
    private boolean isFullscreen;
    private boolean isPortraitFullscreen = false;
    private final Handler handler = new Handler();
    private ContentResolver contentResolver;
    private AutomaticRotationObserver rotationObserver;

    public VideoPlayerFragment() {
        // Required empty public constructor
    }

    // DateFormatter.formatDate(video.getPublishedDate().getValue(), getResources())

    private final Runnable delayedHideSysUi = () -> {
        if(isFullscreen && isAdded()) turnImmersionModeOn();
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        window = requireActivity().getWindow();
        originalSystemUiVisibility = window.getDecorView().getSystemUiVisibility();
        window.getDecorView().setOnSystemUiVisibilityChangeListener(
                visibility -> {
                    if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                        handler.postDelayed(delayedHideSysUi, 2000);
                    }
                }
        );

        if (getArguments() != null) {
            video = getArguments().getParcelable("video");
        }

        contentResolver = requireContext().getContentResolver();
        rotationObserver = new AutomaticRotationObserver(
                new Handler(),
                contentResolver,
                () -> {
                    if (rotationObserver.isAutoRotationEnabled() && isFullscreen) {
                        requireActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                    }
                }
        );
        VideoInfosFragment videoInfosFragment = VideoInfosFragment.newInstance(video);
        getChildFragmentManager().beginTransaction()
                .replace(R.id.youtube_player_infos, videoInfosFragment)
                .commit();
    }

    @Override
    public void onResume() {
        super.onResume();
        registerRotationObserver();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterRotationObserver();
    }

    @Override
    public void onStop() {
        super.onStop();
        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        turnImmersionModeOff();
        requireActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
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

        View videoInfo = view.findViewById(R.id.youtube_player_infos);
        FloatingActionButton portraitFullscreenFab = view.findViewById(R.id.youtube_player_fullscreen_portrait_button);

        youTubePlayerView.addFullscreenListener(new FullscreenListener() {
            @Override
            public void onEnterFullscreen(@NonNull View fullscreenView, @NonNull Function0<Unit> exitFullscreen) {
                isFullscreen = true;
                youTubePlayerView.setVisibility(View.GONE);
                videoInfo.setVisibility(View.GONE);
                portraitFullscreenFab.setVisibility(View.GONE);
                fullscreenViewContainer.setVisibility(View.VISIBLE);
                fullscreenViewContainer.addView(fullscreenView);
                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                    requireActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    handler.postDelayed(
                            () -> {
                                if (isAdded() && rotationObserver.isAutoRotationEnabled())
                                    requireActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                            },
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
                videoInfo.setVisibility(View.VISIBLE);
                fullscreenViewContainer.setVisibility(View.GONE);
                fullscreenViewContainer.removeAllViews();
                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    requireActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    handler.postDelayed(
                            () -> {
                                if (isAdded() && !isFullscreen)
                                    requireActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                            },
                            3*1000
                    );
                }
                turnImmersionModeOff();
                portraitFullscreenFab.setVisibility(View.VISIBLE);
            }
        });

        youTubePlayerView.initialize(
                playerListener,
                playerOptions
        );

        portraitFullscreenFab.setAlpha(0.8f);
        portraitFullscreenFab.setOnClickListener(v -> {
            if (isPortraitFullscreen) {
                youTubePlayerView.wrapContent();
                portraitFullscreenFab.setAlpha(0.8f);
            } else {
                youTubePlayerView.matchParent();
                portraitFullscreenFab.setAlpha(0.4f);
            }
            isPortraitFullscreen = !isPortraitFullscreen;
        });

        addBackPressedCallback();
        return view;
    }


    private void addBackPressedCallback() {
        OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (isFullscreen) {
                    youTubePlayerWhenReady.toggleFullscreen();
                } else {
                    if (youTubePlayerWhenReady != null)
                        youTubePlayerWhenReady.pause();
                    requireActivity().finish();
                }
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), onBackPressedCallback);
    }

    private void turnImmersionModeOn() {
        int immersiveModeFlags = View.SYSTEM_UI_FLAG_IMMERSIVE
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        window.getDecorView().setSystemUiVisibility(immersiveModeFlags);
    }

    private void turnImmersionModeOff() {
        window.getDecorView().setSystemUiVisibility(originalSystemUiVisibility);
    }

    private void registerRotationObserver() {
        contentResolver.registerContentObserver(
                Settings.System.getUriFor(Settings.System.ACCELEROMETER_ROTATION),
                true,
                rotationObserver
        );
    }

    private void unregisterRotationObserver() {
        contentResolver.unregisterContentObserver(rotationObserver);
    }

}