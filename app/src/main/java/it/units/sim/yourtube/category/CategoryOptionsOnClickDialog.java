package it.units.sim.yourtube.category;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import it.units.sim.yourtube.R;
import it.units.sim.yourtube.model.Category;

public class CategoryOptionsOnClickDialog extends DialogFragment {

    public static final String TAG = "SELECT_CHANNELS_FOR_CATEGORY_DIALOG";
    public static final String REQUEST_KEY = "SELECT_ACTION";
    public static final String RESULT_KEY = "ACTION";
    public static final int ACTION_EDIT = 0;
    public static final int ACTION_DELETE = 1;
    private static final String ARG = "category";
    private Bundle result;

    public static CategoryOptionsOnClickDialog newInstance(Category category) {
        CategoryOptionsOnClickDialog dialogFragment = new CategoryOptionsOnClickDialog();
        Bundle args = new Bundle();
        args.putParcelable(ARG, category);
        dialogFragment.setArguments(args);
        return dialogFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        result = new Bundle();
        if (getArguments() == null) {
            return new MaterialAlertDialogBuilder(requireContext())
                    .setMessage(getString(R.string.something_went_wrong))
                    .create();
        }
        Category category = getArguments().getParcelable(ARG);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_category_options, null);
        View modify = dialogView.findViewById(R.id.dialog_category_option_modify);
        View delete = dialogView.findViewById(R.id.dialog_category_option_delete);

        modify.setOnClickListener(v -> {
            result.putInt(RESULT_KEY, ACTION_EDIT);
            dismiss();
        });
        delete.setOnClickListener(v -> {
            result.putInt(RESULT_KEY, ACTION_DELETE);
            dismiss();
        });

        return new MaterialAlertDialogBuilder(requireContext())
                .setTitle(category.getName())
                .setView(dialogView)
                .create();
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        getParentFragmentManager().setFragmentResult(REQUEST_KEY, result);
    }
}
