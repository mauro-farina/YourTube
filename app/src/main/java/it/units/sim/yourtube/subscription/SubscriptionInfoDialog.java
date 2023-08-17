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

    public final static String TAG = "SUBSCRIPTION_INFO_DIALOG";
    private final static String ARG = "subscription";

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
                    .setMessage("Error")
                    .create();
        }

        UserSubscription subscription = getArguments().getParcelable(ARG);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_subscription_info, null);
        TextView channelName = dialogView.findViewById(R.id.dialog_subscription_info_channel_name);
        TextView subscribedSince = dialogView.findViewById(R.id.dialog_subscription_info_sub_since);
        ImageView channelThumbnail = dialogView.findViewById(R.id.dialog_subscription_info_channel_thumbnail);

        channelName.setText("@" + subscription.getChannelName());
        subscribedSince.setText("Subscribed since " + subscription.getReadableSubscribedSince());
        Picasso
                .get()
                .load(subscription.getThumbnailUrl())
                .into(channelThumbnail);

        return new MaterialAlertDialogBuilder(requireContext())
                .setView(dialogView)
                .setNegativeButton("Close", (dialog, which) -> dialog.dismiss())
                .create();
    }
}
