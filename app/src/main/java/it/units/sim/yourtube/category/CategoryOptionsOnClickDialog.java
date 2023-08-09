package it.units.sim.yourtube.category;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;

import it.units.sim.yourtube.R;
import it.units.sim.yourtube.model.Category;

public class CategoryOptionsOnClickDialog extends DialogFragment {

    private CategoriesViewModel categoriesViewModel;
    private NavController navController;

    public static CategoryOptionsOnClickDialog newInstance(Category category) {
        CategoryOptionsOnClickDialog dialogFragment = new CategoryOptionsOnClickDialog();
        Bundle args = new Bundle();
        args.putParcelable("category", category);
        dialogFragment.setArguments(args);
        return dialogFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        categoriesViewModel = new ViewModelProvider(requireActivity()).get(CategoriesViewModel.class);
        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        if (getArguments() == null) {
            return new MaterialAlertDialogBuilder(requireContext())
                    .setMessage("Error")
                    .create();
        }
        Category category = getArguments().getParcelable("category");
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_category_options, null);
        TextView modifyTextView = dialogView.findViewById(R.id.dialog_category_option_modify);
        TextView deleteTextView = dialogView.findViewById(R.id.dialog_category_option_delete);

        modifyTextView.setOnClickListener(v -> {
            Bundle extras = new Bundle();
            extras.putInt("categoryId", category.getId());
            extras.putString("categoryName", category.getName());
            extras.putInt("categoryIcon", category.getDrawableIconResId());
            extras.putStringArrayList("categoryChannels", new ArrayList<>(category.getChannelIds()));
            navController.navigate(R.id.categoryEditFragment, extras);
            dismiss();
        });

        deleteTextView.setOnClickListener(v -> {
            categoriesViewModel.deleteCategory(category);
            dismiss();
        });

        return new MaterialAlertDialogBuilder(requireContext())
                .setTitle(category.getName())
                .setView(dialogView)
                .create();
    }
}
