package it.units.sim.yourtube.subscription;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.squareup.picasso.Picasso;

import it.units.sim.yourtube.R;
import it.units.sim.yourtube.model.UserSubscription;

public class SubscriptionInfoDialog extends DialogFragment {

    public static final String TAG = "SUBSCRIPTION_INFO_DIALOG";
    private static final String ARG = "subscription";

    public static SubscriptionInfoDialog newInstance(UserSubscription subscription) {
        Bundle args = new Bundle();
        args.putParcelable(ARG, subscription);
        SubscriptionInfoDialog dialog = new SubscriptionInfoDialog();
        dialog.setArguments(args);
        return dialog;
    }

    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        if (getArguments() == null || getArguments().getParcelable(ARG) == null) {
            return new MaterialAlertDialogBuilder(requireContext())
                    .setMessage(getString(R.string.something_went_wrong))
                    .create();
        }

        UserSubscription subscription = getArguments().getParcelable(ARG);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_subscription_info, null);
        TextView channelName = dialogView.findViewById(R.id.dialog_subscription_info_channel_name);
        TextView subscribedSince = dialogView.findViewById(R.id.dialog_subscription_info_sub_since);
        ImageView channelThumbnail = dialogView.findViewById(R.id.dialog_subscription_info_channel_thumbnail);

        channelName.setText(subscription.getChannelName());
        subscribedSince.setText(getString(R.string.subscribed_since, subscription.getReadableSubscribedSince()));
        Picasso
                .get()
                .load(subscription.getThumbnailUrl())
                .into(channelThumbnail);

        return new MaterialAlertDialogBuilder(requireContext())
                .setView(dialogView)
                .setNegativeButton(getString(R.string.close), (dialog, which) -> dialog.dismiss())
                .create();
    }
}
