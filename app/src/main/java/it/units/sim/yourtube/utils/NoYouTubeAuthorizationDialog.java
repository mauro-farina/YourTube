package it.units.sim.yourtube.utils;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import it.units.sim.yourtube.MainActivity;
import it.units.sim.yourtube.R;

public class NoYouTubeAuthorizationDialog extends DialogFragment {

    public static final String TAG = "NO YOUTUBE AUTHORIZATION DIALOG";

    public static NoYouTubeAuthorizationDialog newInstance() {
        return new NoYouTubeAuthorizationDialog();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new MaterialAlertDialogBuilder(requireContext())
                .setMessage(getString(R.string.missing_youtube_authorization))
                .setNeutralButton(getString(R.string.ok), (dialog, which) -> dialog.dismiss())
                .show();
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        ((MainActivity) requireActivity()).logoutViaAuthenticationActivity();
    }
}
