package it.units.sim.yourtube;

import android.graphics.Typeface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import it.units.sim.yourtube.model.Category;
import it.units.sim.yourtube.model.UserSubscription;

public class NewCategoryFragment extends Fragment {

    private List<UserSubscription> selectedChannels;
    private MainViewModel subscriptionsViewModel;
    private CategoriesViewModel categoriesViewModel;
    private String categoryIcon;

    public NewCategoryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        categoriesViewModel = new ViewModelProvider(requireActivity()).get(CategoriesViewModel.class);
        categoriesViewModel.fetchCategories();
        subscriptionsViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        selectedChannels = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, 
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_category, container, false);
        List<UserSubscription> subscriptions = subscriptionsViewModel
                                .getSubscriptionsList()
                                .getValue();
        SubscriptionsAdapter subscriptionsAdapter = new SubscriptionsAdapter(
                subscriptions,
                clickedView -> {
                    if (subscriptions == null || subscriptions.size() == 0)
                        return;
                    TextView selectedChannelTextView = clickedView.findViewById(R.id.list_item_subscription_channel_name);
                    String selectedChannelName = selectedChannelTextView.getText().toString();
                    UserSubscription selectedChannel = subscriptions
                            .stream()
                            .filter(sub -> sub.getChannelName().equals(selectedChannelName))
                            .findFirst()
                            .orElse(null);
                    if (selectedChannel == null) {
                        System.err.println("Something very wrong just happened...");
                        return;
                    }
                    if (selectedChannels.contains(selectedChannel)) {
                        selectedChannelTextView.setTypeface(selectedChannelTextView.getTypeface(), Typeface.ITALIC);
                        selectedChannels.remove(selectedChannel);
                    } else {
                        selectedChannelTextView.setTypeface(selectedChannelTextView.getTypeface(), Typeface.BOLD);
                        selectedChannels.add(selectedChannel);
                    }
                }
        );

        //expand subscriptions button
        RecyclerView recyclerView = view.findViewById(R.id.new_category_subscriptions_list);
        ChipGroup chipGroup = view.findViewById(R.id.new_category_subscriptions_chips);
        Button expandSubscriptionRecyclerView = view.findViewById(R.id.new_category_subscriptions_list_expand);
        expandSubscriptionRecyclerView.setOnClickListener(expandBtn -> {
            if (recyclerView.getVisibility() == View.GONE) {
                chipGroup.removeAllViews();
            } else {
                for (UserSubscription sub : selectedChannels) {
                    Chip chip = new Chip(requireContext());
                    chip.setText(sub.getChannelName());
                    chip.setClickable(false);
                    chip.setCheckable(false);
                    chipGroup.addView(chip);
                }
            }
            toggleVisibility(recyclerView);
        });

        // Icon picker
        GridLayout iconsGridLayout = view.findViewById(R.id.category_icons);
        for (int i = 0; i < iconsGridLayout.getChildCount(); i++) {
            View childView = iconsGridLayout.getChildAt(i);
            if (!(childView instanceof ImageView)) continue;
            ImageView imageView = (ImageView) childView;
            imageView.setOnClickListener(v -> {
                categoryIcon = v.getTag().toString();
                toggleVisibility(iconsGridLayout);
            });
        }

        Button expandIconPicker = view.findViewById(R.id.new_category_icons_list_expand);
        expandIconPicker.setOnClickListener(expandBtn -> toggleVisibility(iconsGridLayout));

        recyclerView.setAdapter(subscriptionsAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        Button createCategoryBtn = view.findViewById(R.id.new_category_create);
        createCategoryBtn.setOnClickListener(btn -> {
            TextInputLayout categoryNameInputLayout = view.findViewById(R.id.new_category_name);
            EditText categoryNameInput = categoryNameInputLayout.getEditText();
            if (categoryNameInput == null)
                return;
            String categoryName = categoryNameInput.getText().toString().trim();
            addCategory(categoryName);
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
            navController.navigate(R.id.categoriesFragment);
        });

        return view;
    }

    private void addCategory(String name) {
        if (name.length() == 0)
            return;
        Toast.makeText(requireContext(), name, Toast.LENGTH_SHORT).show();
        List<String> selectedChannelsId = selectedChannels
                .stream()
                .map(UserSubscription::getChannelId)
                .collect(Collectors.toList());
        Category newCategory = new Category(name, selectedChannelsId, "");
        categoriesViewModel.addCategory(newCategory);
    }

    private void toggleVisibility(View view) {
        if (view.getVisibility() == View.VISIBLE) {
            view.setVisibility(View.GONE);
        } else {
            view.setVisibility(View.VISIBLE);
        }
    }

}