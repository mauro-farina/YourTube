package it.units.sim.yourtube.video;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

import it.units.sim.yourtube.YouTubeDataViewModel;
import it.units.sim.yourtube.R;
import it.units.sim.yourtube.model.Category;
import it.units.sim.yourtube.model.UserSubscription;
import it.units.sim.yourtube.model.VideoData;

public class VideosFragment extends Fragment {

    private SimpleDateFormat dateFormat;
    private YouTubeDataViewModel globalViewModel;
    private VideosViewModel localViewModel;
    private VideosAdapter adapter;
    private Calendar calendar;
    private Button datePicker;
    private Button categoryFilterButton;
    private Date dateObserverBypass;
    private List<UserSubscription> subscriptionsObserverBypass;
    private boolean hasDateChangedWhileCategoryFilterOn;

    public VideosFragment() {
        super(R.layout.fragment_videos);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewModelProvider.AndroidViewModelFactory factory =
                ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().getApplication());
        globalViewModel = new ViewModelProvider(requireActivity(), factory).get(YouTubeDataViewModel.class);
        localViewModel = new ViewModelProvider(this).get(VideosViewModel.class);
        dateObserverBypass = localViewModel.getDateFilter().getValue();
        subscriptionsObserverBypass = globalViewModel.getSubscriptionsList().getValue();
        calendar = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
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
        dateObserverBypass = localViewModel.getDateFilter().getValue();
        subscriptionsObserverBypass = globalViewModel.getSubscriptionsList().getValue();
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_videos, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.videos_recycler_view);
        adapter = new VideosAdapter(globalViewModel.getVideosList().getValue(), clickedView -> {
            VideoData video = (VideoData) clickedView.getTag();
            Bundle extras = new Bundle();
            extras.putParcelable("video", video);
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
            navController.navigate(R.id.videoPlayerFragment, extras);
        });
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        categoryFilterButton = view.findViewById(R.id.category_filter_button);
        datePicker = view.findViewById(R.id.date_filter_pick);
        datePicker.setText(dateFormat.format(Objects.requireNonNull(localViewModel.getDateFilter().getValue())));
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
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        globalViewModel.getSubscriptionsList().observe(getViewLifecycleOwner(), list -> {
            if (subscriptionsObserverBypass.equals(list))
                return;
            globalViewModel.fetchVideos(
                    localViewModel.getDateFilter().getValue(),
                    localViewModel.getCategoryFilter().getValue()
            );
        });
        localViewModel.getDateFilter().observe(getViewLifecycleOwner(), date -> {
            if ((this.dateObserverBypass != null) && this.dateObserverBypass.equals(date))
                return;
            globalViewModel.fetchVideos(
                    localViewModel.getDateFilter().getValue(),
                    localViewModel.getCategoryFilter().getValue()
            );
            datePicker.setText(dateFormat.format(date));
            if (localViewModel.getCategoryFilter().getValue() != null) {
                hasDateChangedWhileCategoryFilterOn = true;
            }
        });
        globalViewModel.getVideosList().observe(getViewLifecycleOwner(), list -> {
            Category filterCategory = localViewModel.getCategoryFilter().getValue();
            if (filterCategory != null) {
                setFilteredVideosListInAdapter(filterCategory);
            } else {
                adapter.setVideosList(list);
            }
        });
        localViewModel.getCategoryFilter().observe(
                getViewLifecycleOwner(),
                category -> {
                    if (hasDateChangedWhileCategoryFilterOn) {
                        hasDateChangedWhileCategoryFilterOn = false;
                        globalViewModel.fetchVideos(
                                localViewModel.getDateFilter().getValue(),
                                localViewModel.getCategoryFilter().getValue()
                        );
                    } else {
                        setFilteredVideosListInAdapter(category);
                    }
                    if (category != null) {
                        categoryFilterButton.setText(category.getName());
                    } else {
                        categoryFilterButton.setText(R.string.category);
                    }
                }
        );
        categoryFilterButton.setOnClickListener(v -> {
            FragmentManager fragmentManager = getChildFragmentManager();
            fragmentManager.setFragmentResultListener(
                    FilterVideosByCategoryDialog.REQUEST_KEY,
                    getViewLifecycleOwner(),
                    (requestKey, result) -> {
                        if (!requestKey.equals(FilterVideosByCategoryDialog.REQUEST_KEY))
                            return;
                        if (result.keySet().size() == 0)
                            return;
                        localViewModel.setCategoryFilter(
                                result.getParcelable(FilterVideosByCategoryDialog.RESULT_KEY)
                        );
                    });
            FilterVideosByCategoryDialog
                    .newInstance()
                    .show(fragmentManager, FilterVideosByCategoryDialog.TAG);
        });
    }

    private void setFilteredVideosListInAdapter(Category filterCategory) {
        if (filterCategory == null) {
            adapter.setVideosList(globalViewModel.getVideosList().getValue());
            return;
        }
        adapter.setVideosList(
                Objects.requireNonNull(globalViewModel.getVideosList().getValue())
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