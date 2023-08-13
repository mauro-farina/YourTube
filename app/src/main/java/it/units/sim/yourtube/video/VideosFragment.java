package it.units.sim.yourtube.video;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

import it.units.sim.yourtube.MainViewModel;
import it.units.sim.yourtube.R;
import it.units.sim.yourtube.category.CategoriesAdapter;
import it.units.sim.yourtube.category.CategoriesViewModel;
import it.units.sim.yourtube.model.Category;
import it.units.sim.yourtube.model.VideoData;

public class VideosFragment extends Fragment {

    private SimpleDateFormat dateFormat;
    private MainViewModel globalViewModel;
    private VideosViewModel localViewModel;
    private VideosAdapter adapter;
    private Calendar calendar;
    private Button datePicker;
    private Date currentDateReference;
    private LiveData<List<Category>> categoriesList;
    private Button categoryFilterButton;
    private Date dateObserverBypass;

    public VideosFragment() {
        super(R.layout.fragment_videos);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewModelProvider.AndroidViewModelFactory factory =
                ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().getApplication());
        globalViewModel = new ViewModelProvider(requireActivity(), factory).get(MainViewModel.class);
        localViewModel = new ViewModelProvider(this).get(VideosViewModel.class);
        dateObserverBypass = localViewModel.getDateFilter().getValue();
        calendar = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        currentDateReference = new Date();
        CategoriesViewModel categoriesViewModel = new ViewModelProvider(requireActivity()).get(CategoriesViewModel.class);
        categoriesList = categoriesViewModel.getCategoriesList();
    }

    @Override
    public void onStop() {
        super.onStop();
        dateObserverBypass = localViewModel.getDateFilter().getValue();
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
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        globalViewModel.getSubscriptionsList().observe(getViewLifecycleOwner(), list -> {
            System.out.println("fetch: SUBS");
            globalViewModel.fetchVideos(
                    localViewModel.getDateFilter().getValue(),
                    localViewModel.getCategoryFilter().getValue()
            );
        });
        localViewModel.getDateFilter().observe(getViewLifecycleOwner(), date -> {
            if ((this.dateObserverBypass != null) && this.dateObserverBypass.equals(date)) {
                return;
            }
            globalViewModel.fetchVideos(
                    localViewModel.getDateFilter().getValue(),
                    localViewModel.getCategoryFilter().getValue()
            );
            datePicker.setText(dateFormat.format(date));
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
                this::setFilteredVideosListInAdapter
        );

        Button previousDateButton = view.findViewById(R.id.date_filter_previous);
        Button nextDateButton = view.findViewById(R.id.date_filter_next);
        datePicker.setOnClickListener(v -> showDatePickerDialog());
        previousDateButton.setOnClickListener(view1 -> {
            calendar.add(Calendar.DAY_OF_MONTH, -1);
            localViewModel.setDateFilter(calendar.getTime());
        });
        nextDateButton.setOnClickListener(view1 -> {
            if (currentDateReference.getTime()-10 < calendar.getTime().getTime()) {
                return;
            }
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            localViewModel.setDateFilter(calendar.getTime());
        });

        categoryFilterButton.setOnClickListener(v -> {
            MaterialAlertDialogBuilder dialog = new MaterialAlertDialogBuilder(requireContext());
            View dialogView = getLayoutInflater().inflate(R.layout.dialog_videos_category_filter, null);
            RecyclerView categoriesRecyclerView = dialogView.findViewById(R.id.dialog_videos_category_filter_list);
            CategoriesAdapter categoriesAdapter = new CategoriesAdapter(
                    new ArrayList<>(),
                    clickedCategoryView -> {
                        TextView categoryNameTextView = clickedCategoryView.findViewById(R.id.list_item_category_name);
                        String categoryName = categoryNameTextView.getText().toString();
                        localViewModel.setCategoryFilter(
                                Objects.requireNonNull(categoriesList.getValue())
                                        .stream()
                                        .filter(c -> c.getName().equals(categoryName))
                                        .findFirst()
                                        .orElse(null)
                        );
                    },
                    CategoriesAdapter.VIEW_CONTEXT_VIDEOS_FILTER
            );
            categoriesRecyclerView.setAdapter(categoriesAdapter);
            categoriesRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            categoriesList.observe(getViewLifecycleOwner(), categoriesAdapter::setCategoriesList);
            dialog.setView(dialogView);
            dialog.show();
        });
    }

    private void setFilteredVideosListInAdapter(Category filterCategory) {
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