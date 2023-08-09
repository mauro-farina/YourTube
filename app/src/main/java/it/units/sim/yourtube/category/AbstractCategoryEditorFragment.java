package it.units.sim.yourtube.category;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

import it.units.sim.yourtube.MainViewModel;
import it.units.sim.yourtube.R;
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

        selectedChannelsChipGroup = view.findViewById(R.id.category_editor_subscriptions_chipgroup);
        Button selectChannelsButton = view.findViewById(R.id.category_editor_subscriptions_list_expand);
        selectChannelsButton.setOnClickListener(btn -> {
            FragmentManager fragmentManager = getChildFragmentManager();
            fragmentManager.setFragmentResultListener(
                    "updateSelectedChannels",
                    getViewLifecycleOwner(),
                    (requestKey, result) -> {
                        if (requestKey.equals("updateSelectedChannels")) {
                            selectedChannels = result.getParcelableArrayList("selectedChannels");
                            System.out.println(selectedChannels);
                            selectedChannelsChipGroup.removeAllViews();
                            for (UserSubscription sub : selectedChannels) {
                                Chip chip = new Chip(requireContext());
                                chip.setText(sub.getChannelName());
                                chip.setClickable(false);
                                chip.setCheckable(false);
                                selectedChannelsChipGroup.addView(chip);
                            }
                        }
                    });
            CategorySelectChannelsDialog
                    .newInstance(subscriptions, selectedChannels)
                    .show(fragmentManager, "Tag123"); // TODO: tag
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
