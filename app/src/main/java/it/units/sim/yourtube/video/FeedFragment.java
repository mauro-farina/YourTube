package it.units.sim.yourtube.video;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

import it.units.sim.yourtube.YourTubeApp;
import it.units.sim.yourtube.data.WatchLaterDatabase;
import it.units.sim.yourtube.utils.DateFormatter;
import it.units.sim.yourtube.YouTubeDataViewModel;
import it.units.sim.yourtube.R;
import it.units.sim.yourtube.model.Category;
import it.units.sim.yourtube.model.UserSubscription;
import it.units.sim.yourtube.model.VideoData;
import it.units.sim.yourtube.videoplayer.VideoPlayerActivity;

public class FeedFragment extends Fragment {

    private YouTubeDataViewModel youTubeDataViewModel;
    private VideosViewModel localViewModel;
    private VideosAdapter adapter;
    private Calendar calendar;
    private Button datePicker;
//    private FloatingActionButton categoryFilterFAB;
//    private Chip categoryFilterChip;
    private View progressIndicator;
    private boolean dateObserverBypass;
    private boolean openedVideoPlayer;
    private List<UserSubscription> subscriptionsObserverBypass;
    private boolean hasDateChangedWhileCategoryFilterOn;
    private WatchLaterDatabase db;

    public FeedFragment() {
        super();  // empty constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewModelProvider.AndroidViewModelFactory factory =
                ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().getApplication());
        youTubeDataViewModel = new ViewModelProvider(requireActivity(), factory).get(YouTubeDataViewModel.class);
        localViewModel = new ViewModelProvider(this).get(VideosViewModel.class);
        dateObserverBypass = false;
        openedVideoPlayer = false;
        subscriptionsObserverBypass = youTubeDataViewModel.getSubscriptionsList().getValue();
        calendar = Calendar.getInstance();
        hasDateChangedWhileCategoryFilterOn = false;
        YourTubeApp app = (YourTubeApp) requireActivity().getApplication();
        db = new WatchLaterDatabase(app.getGoogleCredential().getSelectedAccountName());
    }

    @Override
    public void onResume() {
        super.onResume();
        ActionBar toolbar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
        if (toolbar != null)
            toolbar.setDisplayHomeAsUpEnabled(false);
    }

    @Override
    public void onStop() {
        super.onStop();
        dateObserverBypass = true;
        subscriptionsObserverBypass = youTubeDataViewModel.getSubscriptionsList().getValue();
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feed, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.videos_recycler_view);
        adapter = new VideosAdapter(youTubeDataViewModel.getFeedVideos().getValue(), clickedView -> {
            VideoData video = (VideoData) clickedView.getTag();
            Bundle extras = new Bundle();
            extras.putParcelable("video", video);
            Intent intent = new Intent(requireActivity(), VideoPlayerActivity.class);
            intent.putExtras(extras);
            openedVideoPlayer = true;
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setItemViewCacheSize(30);
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            private final int bottomSpace = 200; // pixels of extra scroll

            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                int position = parent.getChildAdapterPosition(view);
                if (position == state.getItemCount() - 1) {
                    outRect.bottom = bottomSpace;
                }
            }
        });


//        categoryFilterFAB = view.findViewById(R.id.category_filter_fab);
//        categoryFilterChip = view.findViewById(R.id.category_filter_chip);
//        datePicker = view.findViewById(R.id.date_filter_pick);
//        datePicker.setOnClickListener(v -> showDatePickerDialog());
//        Button previousDateButton = view.findViewById(R.id.date_filter_previous);
//        Button nextDateButton = view.findViewById(R.id.date_filter_next);
//        previousDateButton.setOnClickListener(view1 -> {
//            calendar.add(Calendar.DAY_OF_MONTH, -1);
//            localViewModel.setDateFilter(calendar.getTime());
//        });
//        nextDateButton.setOnClickListener(view1 -> {
//            calendar.add(Calendar.DAY_OF_MONTH, 1);
//            localViewModel.setDateFilter(calendar.getTime());
//        });

        datePicker = view.findViewById(R.id.date_filter_button);

        GestureDetector gestureDetector = new GestureDetector(requireContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(@NonNull MotionEvent motionEvent) {
                showDatePickerDialog();
                return false;
            }

            @Override
            public boolean onFling(@NonNull MotionEvent motionEvent, @NonNull MotionEvent motionEvent1, float v, float v1) {
                double dx = motionEvent1.getX() - motionEvent.getX();
                double dy = motionEvent1.getY() - motionEvent.getY();

                if (dx > 150 && Math.abs(dy) < 80) {
                    calendar.add(Calendar.DAY_OF_MONTH, -1);
                    localViewModel.setDateFilter(calendar.getTime());
                } else if (dx < 150 && Math.abs(dy) < 80) {
                    calendar.add(Calendar.DAY_OF_MONTH, 1);
                    localViewModel.setDateFilter(calendar.getTime());
                }
                return false;
            }
        });

        datePicker.setOnTouchListener(
                (view1, motionEvent) -> gestureDetector.onTouchEvent(motionEvent)
        );

        progressIndicator = view.findViewById(R.id.feed_fetch_progress);

        Drawable watchLaterIcon = ContextCompat.getDrawable(requireContext(), R.drawable.icon_watch_later);
        final int intrinsicWidth = watchLaterIcon != null ? watchLaterIcon.getIntrinsicWidth() : 0;
        final int intrinsicHeight = watchLaterIcon != null ? watchLaterIcon.getIntrinsicHeight() : 0;

        // Swipe to add to "Watch Later"
        ItemTouchHelper.SimpleCallback callback = new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                return false; // no drag & drop
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
//                int pos = viewHolder.getBindingAdapterPosition();
                VideoData video = (VideoData) viewHolder.itemView.getTag();
                db.addVideo(video);
                Toast.makeText(requireContext(), "Saved in WatchLater", Toast.LENGTH_SHORT).show();

                int pos = viewHolder.getAdapterPosition();
                if (pos == RecyclerView.NO_POSITION) return;

                recyclerView.post(() -> adapter.notifyItemChanged(pos));
            }

            @Override
            public void onChildDraw(@NonNull Canvas c,
                                    @NonNull RecyclerView recyclerView,
                                    @NonNull RecyclerView.ViewHolder viewHolder,
                                    float dX, float dY,
                                    int actionState,
                                    boolean isCurrentlyActive) {

                View itemView = viewHolder.itemView;
                int itemHeight = itemView.getBottom() - itemView.getTop();

                // Draw only the icon, no colored background
                if (watchLaterIcon != null) {
                    // Scale icon if it's too big (optional: keep aspect ratio)
                    int iconSize = Math.min(intrinsicHeight, (int) (itemHeight * 0.6f));
                    int top = itemView.getTop() + (itemHeight - iconSize) / 2;
                    int bottom = top + iconSize;

                    int alpha = Math.min(255, (int) (255 * (Math.abs(dX) / (float) itemView.getWidth())));
                    watchLaterIcon.setAlpha(alpha);

                    if (dX > 0) { // swiping right: icon on left
                        int left = itemView.getLeft() + (itemHeight - iconSize) / 2;
                        int right = left + iconSize;
                        watchLaterIcon.setBounds(left, top, right, bottom);
                    } else if (dX < 0) { // swiping left: icon on right
                        int right = itemView.getRight() - (itemHeight - iconSize) / 2;
                        int left = right - iconSize;
                        watchLaterIcon.setBounds(left, top, right, bottom);
                    } else {
                        // no displacement: don't draw
                        watchLaterIcon.setBounds(0, 0, 0, 0);
                    }

                    c.save();
                    watchLaterIcon.draw(c);
                    c.restore();
                }

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }

            @Override
            public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                super.clearView(recyclerView, viewHolder);
                // ensure translation is zeroed
                viewHolder.itemView.setTranslationX(0);
            }
        };

        new ItemTouchHelper(callback).attachToRecyclerView(recyclerView);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        youTubeDataViewModel.getSubscriptionsList().observe(getViewLifecycleOwner(), list -> {
            if (subscriptionsObserverBypass.equals(list))
                return;
            youTubeDataViewModel.fetchVideos(
                    localViewModel.getDateFilter().getValue(),
                    localViewModel.getCategoryFilter().getValue()
            );
        });
        localViewModel.getDateFilter().observe(getViewLifecycleOwner(), date -> {
            datePicker.animate()
                    .alpha(0f)
                    .setDuration(150)
                    .withEndAction(() -> {
                        datePicker.setText(DateFormatter.formatDate(date.getTime(), getResources()));
                        datePicker.setAlpha(0f);
                        datePicker.animate()
                                .alpha(1f)
                                .setDuration(150)
                                .start();
                    })
                    .start();

            if (openedVideoPlayer) {
                openedVideoPlayer = false;
                dateObserverBypass = false;
            }
            if (dateObserverBypass) {
                dateObserverBypass = false;
                return;
            }
            youTubeDataViewModel.fetchVideos(
                    localViewModel.getDateFilter().getValue(),
                    localViewModel.getCategoryFilter().getValue()
            );
            if (localViewModel.getCategoryFilter().getValue() != null) {
                hasDateChangedWhileCategoryFilterOn = true;
            }
        });
        youTubeDataViewModel.getFeedVideos().observe(getViewLifecycleOwner(), list -> adapter.setVideosList(list));
//        localViewModel.getCategoryFilter().observe(
//                getViewLifecycleOwner(),
//                category -> {
//                    if (hasDateChangedWhileCategoryFilterOn) {
//                        hasDateChangedWhileCategoryFilterOn = false;
//                        youTubeDataViewModel.fetchVideos(
//                                localViewModel.getDateFilter().getValue(),
//                                localViewModel.getCategoryFilter().getValue()
//                        );
//                    } else {
//                        setFilteredVideosListInAdapter(category);
//                    }
//                    if (category != null) {
//                        categoryFilterChip.setText(category.getName());
//                        categoryFilterChip.setVisibility(View.VISIBLE);
//                        categoryFilterChip.setOnCloseIconClickListener(v -> {
//                            v.setVisibility(View.GONE);
//                            localViewModel.setCategoryFilter(null);
//                        });
//                    } else {
//                        categoryFilterChip.setVisibility(View.GONE);
//                    }
//                }
//        );
//        categoryFilterFAB.setOnClickListener(v -> {
//            FragmentManager fragmentManager = getChildFragmentManager();
//            fragmentManager.setFragmentResultListener(
//                    FilterVideosByCategoryDialog.REQUEST_KEY,
//                    getViewLifecycleOwner(),
//                    (requestKey, result) -> {
//                        if (!requestKey.equals(FilterVideosByCategoryDialog.REQUEST_KEY))
//                            return;
//                        if (result.keySet().size() == 0)
//                            return;
//                        localViewModel.setCategoryFilter(
//                                result.getParcelable(FilterVideosByCategoryDialog.RESULT_KEY)
//                        );
//                    });
//            FilterVideosByCategoryDialog
//                    .newInstance()
//                    .show(fragmentManager, FilterVideosByCategoryDialog.TAG);
//        });
        youTubeDataViewModel.getFeedFetchCounter().observe(getViewLifecycleOwner(), fetchCount -> {
            if (fetchCount <= 1) {
                progressIndicator.setVisibility(View.GONE);
            } else {
                progressIndicator.setVisibility(View.VISIBLE);
            }
        });
    }

    private void setFilteredVideosListInAdapter(Category filterCategory) {
        if (youTubeDataViewModel.getFeedVideos().getValue() == null)
            return;
        if (filterCategory == null) {
            adapter.setVideosList(youTubeDataViewModel.getFeedVideos().getValue());
            return;
        }
        adapter.setVideosList(
                youTubeDataViewModel.getFeedVideos().getValue()
                        .stream()
                        .filter(v -> filterCategory.getChannelIds().contains(v.getChannel().getChannelId()))
                        .collect(Collectors.toList())
        );
    }

    private void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getContext(),
                (view, year, monthOfYear, dayOfMonth) -> {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, monthOfYear);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    localViewModel.setDateFilter(calendar.getTime());
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

}