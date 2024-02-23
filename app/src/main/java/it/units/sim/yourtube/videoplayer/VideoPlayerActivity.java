package it.units.sim.yourtube.videoplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import it.units.sim.yourtube.R;

public class VideoPlayerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();
        VideoPlayerFragment fragment = new VideoPlayerFragment();
        fragment.setArguments(extras);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container_view, fragment)
                .commit();

        setContentView(R.layout.activity_video_player);
    }

}