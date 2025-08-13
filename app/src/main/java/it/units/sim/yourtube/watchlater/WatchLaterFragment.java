package it.units.sim.yourtube.watchlater;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import it.units.sim.yourtube.R;
import it.units.sim.yourtube.YourTubeApp;
import it.units.sim.yourtube.data.WatchLaterDatabase;
import it.units.sim.yourtube.data.WatchLaterViewModel;
import it.units.sim.yourtube.model.VideoData;
import it.units.sim.yourtube.video.VideosAdapter;
import it.units.sim.yourtube.videoplayer.VideoPlayerActivity;


public class WatchLaterFragment extends Fragment {

    private WatchLaterViewModel viewModel;
    private WatchLaterDatabase db;
    private VideosAdapter adapter;

    public static WatchLaterFragment newInstance() {
        WatchLaterFragment fragment = new WatchLaterFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        YourTubeApp app = (YourTubeApp) requireActivity().getApplication();
        ViewModelProvider.AndroidViewModelFactory factory =
                ViewModelProvider.AndroidViewModelFactory.getInstance(app);
        viewModel = new ViewModelProvider(requireActivity(), factory).get(WatchLaterViewModel.class);
        db = new WatchLaterDatabase(app.getGoogleCredential().getSelectedAccountName());
    }

    @Override
    public void onResume() {
        super.onResume();
        viewModel.updateList();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_watch_later, container, false);
        RecyclerView listView = view.findViewById(R.id.watchlater_recycler_view);

        adapter = new VideosAdapter(
                new ArrayList<>(),
                v -> {
                    VideoData video = (VideoData) v.getTag();
                    Bundle extras = new Bundle();
                    extras.putParcelable("video", video);
                    Intent intent = new Intent(requireActivity(), VideoPlayerActivity.class);
                    intent.putExtras(extras);
                    startActivity(intent);
                },
                false
        );

        listView.setAdapter(adapter);
        listView.setLayoutManager(new LinearLayoutManager(requireContext()));

        ItemTouchHelper.SimpleCallback callback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                return false; // no drag & drop
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int pos = viewHolder.getAdapterPosition();
                VideoData removed = adapter.removeAt(pos);
                db.removeVideo(removed);
            }

            @Override
            public void onChildDraw(@NonNull Canvas c,
                                    @NonNull RecyclerView recyclerView,
                                    @NonNull RecyclerView.ViewHolder viewHolder,
                                    float dX, float dY,
                                    int actionState,
                                    boolean isCurrentlyActive) {
                View itemView = viewHolder.itemView;
                Paint paint = new Paint();

                if (dX < 0) {
                    paint.setColor(Color.parseColor("#D32F2F"));
                    c.drawRect(itemView.getRight() + dX, itemView.getTop(),
                            itemView.getRight(), itemView.getBottom(), paint);
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };

        new ItemTouchHelper(callback).attachToRecyclerView(listView);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel.getVideosLiveData().observe(
                getViewLifecycleOwner(),
                list -> adapter.setVideosList(list)
        );
    }
}