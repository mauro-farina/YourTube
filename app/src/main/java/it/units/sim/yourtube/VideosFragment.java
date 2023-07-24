package it.units.sim.yourtube;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class VideosFragment extends Fragment {

    private SimpleDateFormat dateFormat;
    private MainViewModel viewModel;
    private VideosAdapter adapter;
    private Calendar calendar;
    private Button datePicker;
    private ImageButton previousDateButton;
    private ImageButton nextDateButton;
    private Date date;
    private Date currentDateReference;

    public VideosFragment() {
        super(R.layout.fragment_videos);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        calendar = Calendar.getInstance();
        date = calendar.getTime();
        currentDateReference = new Date();
        dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        if (Objects.requireNonNull(viewModel.getSubscriptionsList().getValue()).size() > 0) {
            viewModel.fetchVideos(date);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_videos, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.videos_recycler_view);
        adapter = new VideosAdapter(viewModel.getVideosList().getValue());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        viewModel.getSubscriptionsList().observe(getViewLifecycleOwner(), list -> viewModel.fetchVideos(date));
        viewModel.getVideosList().observe(getViewLifecycleOwner(), adapter::setVideosList);

        datePicker = view.findViewById(R.id.date_filter_pick);
        previousDateButton = view.findViewById(R.id.date_filter_previous);
        nextDateButton = view.findViewById(R.id.date_filter_next);
        datePicker = view.findViewById(R.id.date_filter_pick);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        datePicker.setText(sdf.format(calendar.getTime()));
        datePicker.setOnClickListener(v -> showDatePickerDialog());
        previousDateButton.setOnClickListener(view1 -> {
            calendar.add(Calendar.DAY_OF_MONTH, -1);
            date = calendar.getTime();
            viewModel.fetchVideos(date);
            datePicker.setText(dateFormat.format(date));
        });
        nextDateButton.setOnClickListener(view1 -> {
            if (currentDateReference.getTime()-10 < calendar.getTime().getTime()) {
                return;
            }
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            date = calendar.getTime();
            viewModel.fetchVideos(date);
            datePicker.setText(dateFormat.format(date));
        });
    }

    private void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getContext(),
                (view, year, monthOfYear, dayOfMonth) -> {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, monthOfYear);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    date = calendar.getTime();
                    viewModel.fetchVideos(calendar.getTime());
                    datePicker.setText(dateFormat.format(calendar.getTime()));
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

}