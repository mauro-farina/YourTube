package it.units.sim.yourtube;

import android.annotation.SuppressLint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

import it.units.sim.yourtube.model.UserSubscription;

public abstract class AbstractCategoryEditorFragment extends Fragment {

    private View rootView;
    protected List<UserSubscription> subscriptions;
    protected List<UserSubscription> selectedChannels;
    protected ChipGroup selectedChannelsChipGroup;
    protected EditText categoryNameEditText;
    protected ImageView categoryIconPreview;
    protected int chosenCategoryResId;
    protected String categoryName;
    protected String failureReason;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainViewModel subscriptionsViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        subscriptions = subscriptionsViewModel
                .getSubscriptionsList()
                .getValue();
        selectedChannels = new ArrayList<>();
    }

    @Override
    public void onResume() {
        super.onResume();
        toggleBottomNav();
    }

    @Override
    public void onStop() {
        super.onStop();
        toggleBottomNav();
    }

    @Override
    public abstract View onCreateView(@NonNull LayoutInflater inflater,
                                      ViewGroup container,
                                      Bundle savedInstanceState);

    @SuppressLint("DiscouragedApi")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rootView = view;
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
                        showFailureFeedbackMessage("Error: channel not found");
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
        RecyclerView subscriptionsRecyclerView = view.findViewById(R.id.category_editor_subscriptions_list);
        selectedChannelsChipGroup = view.findViewById(R.id.category_editor_subscriptions_chipgroup);
        Button expandSubscriptionsRecyclerView = view.findViewById(R.id.category_editor_subscriptions_list_expand);
        expandSubscriptionsRecyclerView.setOnClickListener(expandBtn -> {
            if (subscriptionsRecyclerView.getVisibility() == View.GONE) {
                selectedChannelsChipGroup.removeAllViews();
            } else {
                for (UserSubscription sub : selectedChannels) {
                    Chip chip = new Chip(requireContext());
                    chip.setText(sub.getChannelName());
                    chip.setClickable(false);
                    chip.setCheckable(false);
                    selectedChannelsChipGroup.addView(chip);
                }
            }
            toggleVisibility(subscriptionsRecyclerView);
        });

        // Icon picker
        GridLayout iconsGridLayout = view.findViewById(R.id.category_editor_icons);

        Button expandIconPicker = view.findViewById(R.id.category_editor_icons_list_expand);
        expandIconPicker.setOnClickListener(expandBtn -> toggleVisibility(iconsGridLayout));

        for (int i = 0; i < iconsGridLayout.getChildCount(); i++) {
            View childView = iconsGridLayout.getChildAt(i);
            if (!(childView instanceof ImageView)) continue;
            ImageView imageView = (ImageView) childView;
            imageView.setOnClickListener(v -> {
                String categoryIconName = v.getTag().toString();
                chosenCategoryResId = getResources().getIdentifier(
                        categoryIconName,
                        "drawable",
                        requireContext().getPackageName()
                );
                toggleVisibility(iconsGridLayout);
                categoryIconPreview.setImageResource(chosenCategoryResId);
            });
        }

        subscriptionsRecyclerView.setAdapter(subscriptionsAdapter);
        subscriptionsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        TextInputLayout categoryNameInputLayout = view.findViewById(R.id.category_editor_name);
        categoryNameEditText = categoryNameInputLayout.getEditText();

        if (categoryNameEditText == null) {
            categoryNameEditText = new EditText(requireContext());
            categoryNameInputLayout.addView(categoryNameEditText);
        }

        Button createCategoryBtn = view.findViewById(R.id.category_editor_action_button);
        createCategoryBtn.setOnClickListener(btn -> {
            categoryName = categoryNameEditText.getText().toString().trim();
            if (createOrModifyCategory()) {
                showSuccessFeedbackMessage();
                NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
                navController.navigate(R.id.categoriesFragment);
            } else {
                showFailureFeedbackMessage(failureReason);
            }
        });

        categoryIconPreview = view.findViewById(R.id.category_editor_icons_preview);
    }

    private void toggleVisibility(View view) {
        if (view.getVisibility() == View.VISIBLE) {
            view.setVisibility(View.GONE);
        } else {
            view.setVisibility(View.VISIBLE);
        }
    }

    protected abstract boolean createOrModifyCategory();

    protected abstract String getSuccessFeedbackMessage();

    protected void showSuccessFeedbackMessage() {
        Snackbar.make(rootView, getSuccessFeedbackMessage(), Snackbar.LENGTH_SHORT).show();
    }

    private void showFailureFeedbackMessage(String failureReason) {
        Snackbar.make(rootView, failureReason, Snackbar.LENGTH_SHORT).show();
    }

    private void toggleBottomNav() {
        View bottomNav = requireActivity().findViewById(R.id.bottom_navigation);
        if (bottomNav.getVisibility() == View.VISIBLE) {
            bottomNav.setVisibility(View.GONE);
        } else {
            bottomNav.setVisibility(View.VISIBLE);
        }
    }


}
