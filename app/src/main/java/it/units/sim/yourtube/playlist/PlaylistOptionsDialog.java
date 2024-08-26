package it.units.sim.yourtube.playlist;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.view.View;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import it.units.sim.yourtube.R;
import it.units.sim.yourtube.model.Playlist;


public class PlaylistOptionsDialog extends DialogFragment {

    public static final String TAG = "PLAYLIST_OPTIONS_DIALOG";
    private static final String ARG = "PLAYLIST";
    public static final String REQUEST_KEY = "SELECT_ACTION";
    public static final String RESULT_KEY = "ACTION";
    public static final int ACTION_EDIT = 0;
    public static final int ACTION_DELETE = 1;

    private Playlist playlist;
    private Bundle result;

    public PlaylistOptionsDialog() {
        // Required empty public constructor
    }

    public static PlaylistOptionsDialog newInstance(Playlist playlist) {
        PlaylistOptionsDialog fragment = new PlaylistOptionsDialog();
        Bundle args = new Bundle();
        args.putSerializable(ARG, playlist);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            playlist = (Playlist) getArguments().getSerializable(ARG);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        result = new Bundle();

        View view = getLayoutInflater().inflate(R.layout.dialog_category_options, null);
        View modify = view.findViewById(R.id.dialog_category_option_modify);
        View delete = view.findViewById(R.id.dialog_category_option_delete);

        modify.setOnClickListener(v -> {
            result.putInt(RESULT_KEY, ACTION_EDIT);
            dismiss();
        });
        delete.setOnClickListener(v -> {
            result.putInt(RESULT_KEY, ACTION_DELETE);
            dismiss();
        });

        return new MaterialAlertDialogBuilder(requireContext())
                .setTitle(playlist.getName())
                .setView(view)
                .create();
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        getParentFragmentManager().setFragmentResult(REQUEST_KEY, result);
    }
}