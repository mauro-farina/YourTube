package it.units.sim.yourtube.category;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import it.units.sim.yourtube.R;
import it.units.sim.yourtube.data.CategoriesViewModel;
import it.units.sim.yourtube.model.Category;
import it.units.sim.yourtube.model.UserSubscription;

public class CategoryNewFragment extends AbstractCategoryEditorFragment {

    private CategoriesViewModel categoriesViewModel;

    public CategoryNewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        categoriesViewModel = new ViewModelProvider(requireActivity()).get(CategoriesViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category_edit, container, false);
        Button actionButton = view.findViewById(R.id.category_editor_action_button);
        actionButton.setText(R.string.create);
        return view;
    }

    @Override
    protected boolean createOrModifyCategory() {
        if (categoryName.length() == 0) {
            failureReason = getString(R.string.new_category_fail_no_name);
            return false;
        }
        if (chosenCategoryIcon == null) {
            failureReason = getString(R.string.new_category_fail_no_icon);
            return false;
        }

        for (Category c : Objects.requireNonNull(categoriesViewModel.getCategoriesList().getValue())) {
            if (c.getName().equals(categoryName)) {
                failureReason = getString(R.string.new_category_fail_category_exists);
                return false;
            }
        }

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(requireContext());
        if (account == null || account.getEmail() == null) {
            return false;
        }

        List<String> selectedChannelsId = Objects
                .requireNonNull(localViewModel.getSelectedChannels().getValue())
                .stream()
                .map(UserSubscription::getChannelId)
                .collect(Collectors.toList());

        Category newCategory = new Category(categoryName, selectedChannelsId, chosenCategoryIcon);
        categoriesViewModel.addCategory(newCategory);
        return true;
    }

    @Override
    protected String getSuccessFeedbackMessage() {
        return getString(R.string.category_created, categoryName);
    }

    @Override
    protected String getToolbarTitle() {
        return getString(R.string.title_new_category);
    }

}