package it.units.sim.yourtube;

import android.graphics.Typeface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import it.units.sim.yourtube.model.Category;
import it.units.sim.yourtube.model.UserSubscription;

public class CategoriesFragment extends Fragment {

    private CategoriesAdapter adapter;
    private CategoriesViewModel categoriesViewModel;
    private MainViewModel subscriptionsViewModel;

    public CategoriesFragment() {
        super(R.layout.fragment_categories);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        subscriptionsViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        categoriesViewModel = new ViewModelProvider(requireActivity()).get(CategoriesViewModel.class);
        categoriesViewModel.fetchCategories();
        adapter = new CategoriesAdapter(new ArrayList<>());
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_categories, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.categories_recycler_view);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        categoriesViewModel.getCategoriesList().observe(getViewLifecycleOwner(), adapter::setCategoriesList);

        FloatingActionButton fab = view.findViewById(R.id.categories_add_category_fab);
        fab.setOnClickListener(v -> {
            // dialog
            View dialogView = LayoutInflater
                    .from(requireContext())
                    .inflate(R.layout.add_category_dialog, null);
            // adapter for recyclerview
            RecyclerView subscriptionsRecyclerView =
                    dialogView.findViewById(R.id.new_category_dialog_channels_list);
            List<String> selectedChannels = new ArrayList<>();
            SubscriptionsAdapter subscriptionsAdapter = new SubscriptionsAdapter(
                    subscriptionsViewModel.getSubscriptionsList().getValue(),
                    clickedView -> {
                        TextView selectedChannelTextView = clickedView.findViewById(R.id.list_item_subscription_channel_name);
                        String selectedChannelName = selectedChannelTextView.getText().toString();
                        UserSubscription selectedChannel = Objects
                                .requireNonNull(
                                    subscriptionsViewModel
                                    .getSubscriptionsList()
                                    .getValue()
                                )
                                .stream()
                                .filter(sub -> sub.getChannelName().equals(selectedChannelName))
                                .findFirst()
                                .orElse(null);
                        if (selectedChannel == null) {
                            System.err.println("Something very wrong just happened...");
                            return;
                        }
                        selectedChannelTextView.setTypeface(selectedChannelTextView.getTypeface(), Typeface.BOLD);
                        selectedChannels.add(selectedChannel.getChannelId());
                    }
            );

            //expand button
            ImageButton expandSubscriptionRecyclerView = dialogView.findViewById(R.id.add_category_dialog_expand_list);
            expandSubscriptionRecyclerView.setOnClickListener(expandBtn -> {
                if (subscriptionsRecyclerView.getVisibility() == View.GONE) {
                    subscriptionsRecyclerView.setVisibility(View.VISIBLE);
                    expandBtn.setRotation(180);
                } else {
                    subscriptionsRecyclerView.setVisibility(View.GONE);
                    expandBtn.setRotation(0);
                }
            });

            subscriptionsRecyclerView.setAdapter(subscriptionsAdapter);
            subscriptionsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            EditText input = dialogView.findViewById(R.id.new_category_dialog_name);

            new MaterialAlertDialogBuilder(requireContext())
                    .setTitle(R.string.new_category_dialog_title)
                    .setView(dialogView)
                    .setPositiveButton("ADD", (dialog, which) -> {
                        System.out.println(selectedChannels);
                        addCategory(input.getText().toString());
                    })
                    .show();
        });
        return view;
    }

    private void addCategory(String name) {
        name = name.trim();
        if (name.length() == 0)
            return;
        Toast.makeText(requireContext(), name, Toast.LENGTH_SHORT).show();
        Category newCategory = new Category(name);
        categoriesViewModel.addCategory(newCategory);
    }
}