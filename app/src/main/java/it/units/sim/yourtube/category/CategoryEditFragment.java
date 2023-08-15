package it.units.sim.yourtube.category;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import it.units.sim.yourtube.R;
import it.units.sim.yourtube.data.CategoriesViewModel;
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
            localViewModel.setSelectedChannels(
                    subscriptions.stream()
                        .filter(s -> previouslySelectedChannelIds.contains(s.getChannelId()))
                        .collect(Collectors.toList())
            );
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
    }

    @Override
    protected boolean createOrModifyCategory() {
        Category categoryToUpdate = Objects
                .requireNonNull(categoriesViewModel.getCategoriesList().getValue())
                .stream()
                .filter(c -> c.getId() == categoryId)
                .findFirst()
                .orElse(null);
        if (categoryToUpdate == null) {
            failureReason = "Channel not found.";
            return false;
        }
        categoryToUpdate.setName(categoryName);
        categoryToUpdate.setChannelIds(Objects
                .requireNonNull(localViewModel.getSelectedChannels().getValue())
                .stream()
                .map(UserSubscription::getChannelId)
                .collect(Collectors.toList())
        );
        categoryToUpdate.setDrawableIconResId(chosenCategoryResId);
        categoriesViewModel.updateCategory(categoryToUpdate);
        return true;
    }

    @Override
    protected String getSuccessFeedbackMessage() {
        return "Category " + categoryName + " modified!";
    }

    @Override
    protected String getToolbarTitle() {
        return "Edit " + categoryName;
    }

}
