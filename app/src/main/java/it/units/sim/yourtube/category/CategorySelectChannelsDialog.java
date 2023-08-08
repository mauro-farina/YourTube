package it.units.sim.yourtube.category;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.List;

import it.units.sim.yourtube.R;
import it.units.sim.yourtube.model.UserSubscription;
import it.units.sim.yourtube.subscription.SubscriptionsAdapter;


public class CategorySelectChannelsDialog extends DialogFragment {

    private Bundle results;
    private List<UserSubscription> subscriptions;
    private List<UserSubscription> newSelectedChannels;


    // https://developer.android.com/reference/androidx/fragment/app/DialogFragment#DialogFragment()
    public CategorySelectChannelsDialog(Bundle extras) {
        setArguments(extras);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        if (getArguments() == null) {
            return new MaterialAlertDialogBuilder(requireContext())
                    .setMessage("Error")
                    .create();
        }

        subscriptions = getArguments().getParcelableArrayList("subscriptions");
        if (subscriptions == null || subscriptions.size() == 0) {
            return new MaterialAlertDialogBuilder(requireContext())
                    .setMessage("You are not subscribed to any channel")
                    .create();
        }

        List<UserSubscription> originalSelectedChannels = getArguments().getParcelableArrayList("selectedChannels");
        newSelectedChannels = new ArrayList<>(originalSelectedChannels);

        results = new Bundle();
        results.putParcelableArrayList(
                "selectedChannels",
                (ArrayList<? extends Parcelable>) originalSelectedChannels
        );

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_category_select_channels, null);
        RecyclerView subscriptionsRecyclerView = dialogView.findViewById(R.id.dialog_category_pick_channels_list);
        SubscriptionsAdapter subscriptionsAdapter = new SubscriptionsAdapter(
                subscriptions,
                clickedView -> {
                    if (subscriptions == null || subscriptions.size() == 0)
                        return;
                    TextView selectedChannelTextView = clickedView.findViewById(R.id.list_item_subscription_channel_name);
                    String selectedChannelName = selectedChannelTextView.getText().toString();
                    UserSubscription selectedChannel = subscriptions
                            .stream()
                            .filter(sub -> sub.getChannelName().equals(selectedChannelName))
                            .findFirst()
                            .orElse(null);
                    if (selectedChannel == null) {
                        return;
                    }
                    if (newSelectedChannels.contains(selectedChannel)) {
                        selectedChannelTextView.setTypeface(selectedChannelTextView.getTypeface(), Typeface.ITALIC);
                        newSelectedChannels.remove(selectedChannel);
                    } else {
                        selectedChannelTextView.setTypeface(selectedChannelTextView.getTypeface(), Typeface.BOLD);
                        newSelectedChannels.add(selectedChannel);
                    }

                }
        );

        subscriptionsRecyclerView.setAdapter(subscriptionsAdapter);
        subscriptionsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        return new MaterialAlertDialogBuilder(requireContext())
                .setView(dialogView)
                .setPositiveButton(
                        getString(R.string.confirm),
                        (dialog, which) -> results.putParcelableArrayList(
                                "selectedChannels",
                                (ArrayList<? extends Parcelable>) newSelectedChannels
                        ))
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .create();
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        getParentFragmentManager().setFragmentResult("updateSelectedChannels", results);
    }
}
