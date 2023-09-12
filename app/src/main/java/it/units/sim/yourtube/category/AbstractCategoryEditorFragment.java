package it.units.sim.yourtube.category;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import java.util.List;

import it.units.sim.yourtube.utils.EmptyMenuProvider;
import it.units.sim.yourtube.YouTubeDataViewModel;
import it.units.sim.yourtube.R;
import it.units.sim.yourtube.model.CategoryIcon;
import it.units.sim.yourtube.model.UserSubscription;

public abstract class AbstractCategoryEditorFragment extends Fragment {

    private ActionBar toolbar;
    protected List<UserSubscription> subscriptions;
    protected CategoryEditorViewModel localViewModel;
    protected ChipGroup selectedChannelsChipGroup;
    protected EditText categoryNameEditText;
    protected ImageView categoryIconPreview;
    protected CategoryIcon chosenCategoryIcon;
    protected String categoryName;
    protected String failureReason;
    private MenuProvider menuProvider;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        YouTubeDataViewModel subscriptionsViewModel = new ViewModelProvider(requireActivity()).get(YouTubeDataViewModel.class);
//        toolbar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
        menuProvider = new EmptyMenuProvider();
        localViewModel = new ViewModelProvider(this).get(CategoryEditorViewModel.class);
        subscriptions = subscriptionsViewModel
                .getSubscriptionsList()
                .getValue();
    }

    @Override
    public void onResume() {
        super.onResume();
        toggleBottomNav();
        toolbar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
        if (toolbar != null) {
            toolbar.setDisplayHomeAsUpEnabled(true);
            toolbar.setTitle(getToolbarTitle());
            requireActivity().addMenuProvider(menuProvider);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        toggleBottomNav();
        requireActivity().removeMenuProvider(menuProvider);
        if (toolbar != null) {
            toolbar.setDisplayHomeAsUpEnabled(false);
            toolbar.setTitle(R.string.app_name);
        }
    }

    @Override
    public abstract View onCreateView(@NonNull LayoutInflater inflater,
                                      ViewGroup container,
                                      Bundle savedInstanceState);

    @SuppressLint("DiscouragedApi")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Channels selector
        selectedChannelsChipGroup = view.findViewById(R.id.category_editor_subscriptions_chipgroup);
        Button selectChannelsButton = view.findViewById(R.id.category_editor_subscriptions_list_expand);
        selectChannelsButton.setOnClickListener(btn -> {
            FragmentManager fragmentManager = getChildFragmentManager();
            fragmentManager.setFragmentResultListener(
                    CategorySelectChannelsDialog.REQUEST_KEY,
                    getViewLifecycleOwner(),
                    (requestKey, result) -> {
                        if (!requestKey.equals(CategorySelectChannelsDialog.REQUEST_KEY))
                            return;
                        if (result.keySet().size() == 0)
                            return;
                        localViewModel.setSelectedChannels(
                                result.getParcelableArrayList(CategorySelectChannelsDialog.RESULT_KEY)
                        );
                    });
            CategorySelectChannelsDialog
                    .newInstance(subscriptions, localViewModel.getSelectedChannels().getValue())
                    .show(fragmentManager, CategorySelectChannelsDialog.TAG);
        });

        // Icon selector
        categoryIconPreview = view.findViewById(R.id.category_editor_icons_preview);
        Button selectIconButton = view.findViewById(R.id.category_editor_icons_list_expand);
        selectIconButton.setOnClickListener(btn -> {
            FragmentManager fragmentManager = getChildFragmentManager();
            fragmentManager.setFragmentResultListener(
                    CategorySelectIconDialog.REQUEST_KEY,
                    getViewLifecycleOwner(),
                    (requestKey, result) -> {
                        if (!requestKey.equals(CategorySelectIconDialog.REQUEST_KEY))
                            return;
                        if (result.keySet().size() == 0)
                            return;
                        chosenCategoryIcon = (CategoryIcon) result.getSerializable(CategorySelectIconDialog.RESULT_KEY);
                        categoryIconPreview.setImageResource(chosenCategoryIcon.getResourceId());
                    });
            CategorySelectIconDialog
                    .newInstance()
                    .show(fragmentManager, CategorySelectIconDialog.TAG);
        });

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
                navController.popBackStack();
            } else {
                showFailureFeedbackMessage(failureReason);
            }
        });

        localViewModel.getSelectedChannels().observe(getViewLifecycleOwner(), list -> {
            selectedChannelsChipGroup.removeAllViews();
            for (UserSubscription sub : list) {
                Chip chip = new Chip(requireContext());
                chip.setText(sub.getChannelName());
                chip.setClickable(false);
                chip.setCheckable(false);
                selectedChannelsChipGroup.addView(chip);
            }
        });
    }

    protected abstract boolean createOrModifyCategory();

    protected abstract String getSuccessFeedbackMessage();

    protected void showSuccessFeedbackMessage() {
        Snackbar.make(requireView(), getSuccessFeedbackMessage(), Snackbar.LENGTH_SHORT).show();
    }

    private void showFailureFeedbackMessage(String failureReason) {
        Snackbar.make(requireView(), failureReason, Snackbar.LENGTH_SHORT).show();
    }

    private void toggleBottomNav() {
        View bottomNav = requireActivity().findViewById(R.id.bottom_navigation);
        if (bottomNav.getVisibility() == View.VISIBLE) {
            bottomNav.setVisibility(View.GONE);
        } else {
            bottomNav.setVisibility(View.VISIBLE);
        }
    }

    protected abstract String getToolbarTitle();

}
