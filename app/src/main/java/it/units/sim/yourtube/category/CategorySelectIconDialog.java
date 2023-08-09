package it.units.sim.yourtube.category;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;


import it.units.sim.yourtube.R;

public class CategorySelectIconDialog extends DialogFragment {

    public static final String TAG = "SELECT_ICON_FOR_CATEGORY_DIALOG";
    private AlertDialog dialog;
    private Bundle result;

    public static CategorySelectIconDialog newInstance(int selectedIconResourceId) {
        CategorySelectIconDialog dialogFragment = new CategorySelectIconDialog();
        Bundle args = new Bundle();
        args.putInt("selectedIconResourceId", selectedIconResourceId);
        dialogFragment.setArguments(args);
        return dialogFragment;
    }

    @SuppressLint("DiscouragedApi")
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        result = new Bundle();

        if (getArguments() == null) {
            return new MaterialAlertDialogBuilder(requireContext())
                    .setMessage("Error")
                    .create();
        }

        int originalSelectedIconResId = getArguments().getInt("selectedIconResourceId");
        result.putInt("selectedIconResourceId", originalSelectedIconResId);

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_category_select_icon, null);
        GridLayout iconsGrid = dialogView.findViewById(R.id.category_select_icon_grid);
        for (int i = 0; i < iconsGrid.getChildCount(); i++) {
            View childView = iconsGrid.getChildAt(i);
            if (!(childView instanceof ImageView)) continue;
            childView.setOnClickListener(v -> {
                String categoryIconName = v.getTag().toString();
                int selectedIconResId = getResources().getIdentifier(
                        categoryIconName,
                        "drawable",
                        requireContext().getPackageName()
                );
                result.putInt("selectedIconResourceId", selectedIconResId);
                dialog.dismiss();
            });
        }

        dialog = new MaterialAlertDialogBuilder(requireContext())
                .setView(dialogView)
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .create();

        return dialog;
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        getParentFragmentManager().setFragmentResult("updateSelectedIcon", result);
    }
}
