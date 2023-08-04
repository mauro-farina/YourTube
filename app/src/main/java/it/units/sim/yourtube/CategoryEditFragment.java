package it.units.sim.yourtube;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.chip.Chip;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import it.units.sim.yourtube.model.Category;
import it.units.sim.yourtube.model.UserSubscription;

public class CategoryEditFragment extends AbstractCategoryEditorFragment {

    private int categoryId;
    private CategoriesViewModel categoriesViewModel;
    private List<String> previouslySelectedChannelIds;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        categoriesViewModel = new ViewModelProvider(requireActivity()).get(CategoriesViewModel.class);

        if (getArguments() != null) {
            categoryId = getArguments().getInt("categoryId");
            categoryName = getArguments().getString("categoryName");
            chosenCategoryResId = getArguments().getInt("categoryIcon");
            previouslySelectedChannelIds = getArguments().getStringArrayList("categoryChannels");
            selectedChannels = subscriptions.stream()
                    .filter(s -> previouslySelectedChannelIds.contains(s.getChannelId()))
                    .collect(Collectors.toList());
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
       View view = inflater.inflate(R.layout.fragment_category_edit, container, false);
       Button actionButton = view.findViewById(R.id.category_editor_action_button);
       actionButton.setText(R.string.edit);
       return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        categoryNameEditText.setText(categoryName);
        if (chosenCategoryResId != 0) {
            categoryIconPreview.setImageResource(chosenCategoryResId);
        }
        for (UserSubscription sub : selectedChannels) {
            Chip chip = new Chip(requireContext());
            chip.setText(sub.getChannelName());
            chip.setClickable(false);
            chip.setCheckable(false);
            selectedChannelsChipGroup.addView(chip);
        }
    }

    @Override
    protected boolean createOrModifyCategory() {
        Category categoryToUpdate = Objects
                .requireNonNull(categoriesViewModel.getCategoriesList().getValue())
                .stream()
                .filter(c -> c.id == categoryId)
                .findFirst()
                .orElse(null);
        if (categoryToUpdate == null) {
            failureReason = "Channel not found.";
            return false;
        }
        categoryToUpdate.name = categoryName;
        categoryToUpdate.channelIds = selectedChannels
                .stream()
                .map(UserSubscription::getChannelId)
                .collect(Collectors.toList());
        categoryToUpdate.drawableIconResId = chosenCategoryResId;
        categoriesViewModel.updateCategory(categoryToUpdate);
        return true;
    }

    @Override
    protected void showSuccessSnackbarMessage(View parentView) {
        Snackbar.make(parentView, "Category " + categoryName + " modified!", Snackbar.LENGTH_SHORT).show();
    }
}
