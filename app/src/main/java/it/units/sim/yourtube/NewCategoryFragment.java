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
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import it.units.sim.yourtube.model.Category;
import it.units.sim.yourtube.model.UserSubscription;

public class NewCategoryFragment extends Fragment {

    private List<UserSubscription> selectedChannels;
    private MainViewModel subscriptionsViewModel;
    private CategoriesViewModel categoriesViewModel;

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
        RecyclerView recyclerView = view.findViewById(R.id.new_category_subscriptions_list);
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
                    selectedChannelTextView.setTypeface(selectedChannelTextView.getTypeface(), Typeface.BOLD);
                    selectedChannels.add(selectedChannel);
                }
        );

        //expand button
        ImageButton expandSubscriptionRecyclerView = view.findViewById(R.id.new_category_subscriptions_list_expand);
        expandSubscriptionRecyclerView.setOnClickListener(expandBtn -> {
            if (recyclerView.getVisibility() == View.GONE) {
                recyclerView.setVisibility(View.VISIBLE);
                expandBtn.setRotation(180);
            } else {
                recyclerView.setVisibility(View.GONE);
                expandBtn.setRotation(0);
            }
        });

        // Icon picker
        GridLayout iconsGridLayout = view.findViewById(R.id.dialog_add_category_icon_picker);
        ImageButton expandIconPicker = view.findViewById(R.id.add_category_dialog_expand_list_icons);
        expandIconPicker.setOnClickListener(expandBtn -> {
            if (iconsGridLayout.getVisibility() == View.GONE) {
                iconsGridLayout.setVisibility(View.VISIBLE);
                expandBtn.setRotation(180);
            } else {
                iconsGridLayout.setVisibility(View.GONE);
                expandBtn.setRotation(0);
            }
        });

        recyclerView.setAdapter(subscriptionsAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        Button createCategoryBtn = view.findViewById(R.id.new_category_create);
        createCategoryBtn.setOnClickListener(btn -> {
            EditText categoryNameInput = view.findViewById(R.id.new_category_name);
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
        Category newCategory = new Category(name, selectedChannelsId);
        categoriesViewModel.addCategory(newCategory);
    }

}