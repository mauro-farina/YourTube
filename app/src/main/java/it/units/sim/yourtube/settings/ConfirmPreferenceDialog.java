package it.units.sim.yourtube.settings;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import it.units.sim.yourtube.R;

public class ConfirmPreferenceDialog extends DialogFragment {

    public static final String TAG = "DIALOG_CONFIRM_PREFERENCE";
    public static final String REQUEST_KEY = "confirm_preference";
    public static final String RESULT_KEY = "preference";
    public static final int PREFERENCE_DELETE_ACCOUNT = 2;
    private Bundle result;

    public static ConfirmPreferenceDialog newInstance(int preference) {
        Bundle args = new Bundle();
        args.putInt(RESULT_KEY, preference);
        ConfirmPreferenceDialog fragment = new ConfirmPreferenceDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        result = new Bundle();

        if (getArguments() == null) {
            return new MaterialAlertDialogBuilder(requireContext())
                    .setMessage(getString(R.string.error))
                    .create();
        }
        int preference = getArguments().getInt(RESULT_KEY);
        MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(requireContext());
        if (preference == PREFERENCE_DELETE_ACCOUNT) {
            dialogBuilder.setTitle(getString(R.string.delete_account));
            dialogBuilder.setMessage(getString(R.string.dialog_delete_account_warning));
        } else {
            dialogBuilder.setMessage(getString(R.string.error));
        }
        dialogBuilder.setNegativeButton(getString(R.string.cancel), (dialog, which) -> dialog.dismiss());
        dialogBuilder.setPositiveButton(getString(R.string.confirm), (dialog, which) -> result.putInt(RESULT_KEY, preference));
        return dialogBuilder.show();
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        getParentFragmentManager().setFragmentResult(REQUEST_KEY, result);
    }

}
