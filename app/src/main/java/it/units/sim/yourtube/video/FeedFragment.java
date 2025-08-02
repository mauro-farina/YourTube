package it.units.sim.yourtube.video;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.material.chip.Chip;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

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
    private Chip categoryFilterChip;
    private View progressIndicator;
    private boolean dateObserverBypass;
    private boolean openedVideoPlayer;
    private List<UserSubscription> subscriptionsObserverBypass;
    private boolean hasDateChangedWhileCategoryFilterOn;

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

//        categoryFilterFAB = view.findViewById(R.id.category_filter_fab);
        categoryFilterChip = view.findViewById(R.id.category_filter_chip);
        datePicker = view.findViewById(R.id.date_filter_pick);
        datePicker.setOnClickListener(v -> showDatePickerDialog());
        Button previousDateButton = view.findViewById(R.id.date_filter_previous);
        Button nextDateButton = view.findViewById(R.id.date_filter_next);
        previousDateButton.setOnClickListener(view1 -> {
            calendar.add(Calendar.DAY_OF_MONTH, -1);
            localViewModel.setDateFilter(calendar.getTime());
        });
        nextDateButton.setOnClickListener(view1 -> {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            localViewModel.setDateFilter(calendar.getTime());
        });
        progressIndicator = view.findViewById(R.id.feed_fetch_progress);
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
            datePicker.setText(DateFormatter.formatDate(date.getTime(), getResources()));
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
        localViewModel.getCategoryFilter().observe(
                getViewLifecycleOwner(),
                category -> {
                    if (hasDateChangedWhileCategoryFilterOn) {
                        hasDateChangedWhileCategoryFilterOn = false;
                        youTubeDataViewModel.fetchVideos(
                                localViewModel.getDateFilter().getValue(),
                                localViewModel.getCategoryFilter().getValue()
                        );
                    } else {
                        setFilteredVideosListInAdapter(category);
                    }
                    if (category != null) {
                        categoryFilterChip.setText(category.getName());
                        categoryFilterChip.setVisibility(View.VISIBLE);
                        categoryFilterChip.setOnCloseIconClickListener(v -> {
                            v.setVisibility(View.GONE);
                            localViewModel.setCategoryFilter(null);
                        });
                    } else {
                        categoryFilterChip.setVisibility(View.GONE);
                    }
                }
        );
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