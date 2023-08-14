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
import androidx.fragment.app.DialogFragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;


import it.units.sim.yourtube.R;

public class CategorySelectIconDialog extends DialogFragment {

    public static final String TAG = "SELECT_ICON_FOR_CATEGORY_DIALOG";
    public static final String REQUEST_KEY = "updateSelectedIcon";
    public static final String RESULT_KEY = "selectedIconResourceId";
    private Bundle result;

    public static CategorySelectIconDialog newInstance() {
        return new CategorySelectIconDialog();
    }

    @SuppressLint("DiscouragedApi")
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        result = new Bundle();

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
                result.putInt(RESULT_KEY, selectedIconResId);
                dismiss();
            });
        }

        return new MaterialAlertDialogBuilder(requireContext())
                .setView(dialogView)
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .create();
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        getParentFragmentManager().setFragmentResult(REQUEST_KEY, result);
    }
}
