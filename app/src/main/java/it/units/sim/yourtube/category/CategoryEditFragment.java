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
    private String originalCategoryName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        categoriesViewModel = new ViewModelProvider(requireActivity()).get(CategoriesViewModel.class);

        if (getArguments() != null) {
            Category category = getArguments().getParcelable("category");
            categoryId = category.getId();
            categoryName = category.getName();
            originalCategoryName = categoryName;
            chosenCategoryIcon = category.getCategoryIcon();
            previouslySelectedChannelIds = category.getChannelIds();
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
        if (chosenCategoryIcon != null) {
            categoryIconPreview.setImageResource(chosenCategoryIcon.getResourceId());
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
            failureReason = getString(R.string.category_not_found);
            return false;
        }
        if (!categoryName.equals(originalCategoryName)) {
            Category alreadyExistingCategory = Objects
                    .requireNonNull(categoriesViewModel.getCategoriesList().getValue())
                    .stream()
                    .filter(c -> c.getName().equals(categoryName))
                    .findFirst()
                    .orElse(null);
            if (alreadyExistingCategory != null) {
                failureReason = "Name already used by another category";
                return false;
            }
        }
        categoryToUpdate.setName(categoryName);
        categoryToUpdate.setChannelIds(Objects
                .requireNonNull(localViewModel.getSelectedChannels().getValue())
                .stream()
                .map(UserSubscription::getChannelId)
                .collect(Collectors.toList())
        );
        categoryToUpdate.setCategoryIcon(chosenCategoryIcon);
        categoriesViewModel.updateCategory(categoryToUpdate);
        return true;
    }

    @Override
    protected String getSuccessFeedbackMessage() {
        return getString(R.string.category_modified, categoryName);
    }

    @Override
    protected String getToolbarTitle() {
        return getString(R.string.title_edit_category, categoryName);
    }

}
