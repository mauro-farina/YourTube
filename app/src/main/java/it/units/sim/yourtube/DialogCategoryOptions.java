package it.units.sim.yourtube;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;

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
            extras.putInt("categoryId", category.id);
            extras.putString("categoryName", category.name);
            extras.putInt("categoryIcon", category.drawableIconResId);
            extras.putStringArrayList("categoryChannels", new ArrayList<>(category.channelIds));
            navController.navigate(R.id.newCategoryFragment, extras);
            dismiss();
        });
        TextView deleteTextView = dialogView.findViewById(R.id.dialog_category_option_delete);
        deleteTextView.setOnClickListener(v -> {
            categoriesViewModel.deleteCategory(category);
            dismiss();
        });

        dialogBuilder.setTitle(category.name);
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
