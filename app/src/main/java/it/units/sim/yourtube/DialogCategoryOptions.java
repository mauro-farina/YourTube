package it.units.sim.yourtube;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import it.units.sim.yourtube.model.Category;

public class DialogCategoryOptions {

    private final MaterialAlertDialogBuilder dialogBuilder;
    private AlertDialog dialog;

    public DialogCategoryOptions(@NonNull Context context, Category category, CategoriesViewModel categoriesViewModel) {
        dialogBuilder = new MaterialAlertDialogBuilder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_category_options, null);
        dialogBuilder.setView(dialogView);
        TextView modifyTextView = dialogView.findViewById(R.id.dialog_category_option_modify);
        modifyTextView.setOnClickListener(v -> {
            Toast.makeText(context, "MODIFY", Toast.LENGTH_SHORT).show();
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
