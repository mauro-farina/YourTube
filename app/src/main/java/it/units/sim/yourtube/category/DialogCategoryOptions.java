package it.units.sim.yourtube.category;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.navigation.NavController;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;

import it.units.sim.yourtube.R;
import it.units.sim.yourtube.model.Category;

public class DialogCategoryOptions {

    private final MaterialAlertDialogBuilder dialogBuilder;
    private AlertDialog dialog;

    public DialogCategoryOptions(@NonNull Context context,
                                 Category category,
                                 CategoriesViewModel categoriesViewModel,
                                 NavController navController) {
        dialogBuilder = new MaterialAlertDialogBuilder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_category_options, null);
        dialogBuilder.setView(dialogView);
        TextView modifyTextView = dialogView.findViewById(R.id.dialog_category_option_modify);
        modifyTextView.setOnClickListener(v -> {
            Bundle extras = new Bundle();
            extras.putInt("categoryId", category.getId());
            extras.putString("categoryName", category.getName());
            extras.putInt("categoryIcon", category.getDrawableIconResId());
            extras.putStringArrayList("categoryChannels", new ArrayList<>(category.getChannelIds()));
            navController.navigate(R.id.categoryEditFragment, extras);
            dismiss();
        });
        TextView deleteTextView = dialogView.findViewById(R.id.dialog_category_option_delete);
        deleteTextView.setOnClickListener(v -> {
            categoriesViewModel.deleteCategory(category);
            dismiss();
        });

        dialogBuilder.setTitle(category.getName());
    }

    public void show() {
        dialog = dialogBuilder.show();
    }

    public void dismiss() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

}
