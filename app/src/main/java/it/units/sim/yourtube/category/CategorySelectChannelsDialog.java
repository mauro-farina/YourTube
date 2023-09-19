package it.units.sim.yourtube.category;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.CheckBox;

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

    public static final String TAG = "SELECT_CHANNELS_FOR_CATEGORY_DIALOG";
    public static final String REQUEST_KEY = "updateSelectedChannels";
    public static final String RESULT_KEY = "selectedChannels";
    private static final String ARG_SUBSCRIPTIONS_KEY = "subscriptions";
    private static final String ARG_SELECTED_CHANNELS_KEY = "selectedChannels";
    private Bundle result;
    private List<UserSubscription> subscriptions;
    private List<UserSubscription> newSelectedChannels;

    public static CategorySelectChannelsDialog newInstance(List<UserSubscription> subscriptions,
                                                           List<UserSubscription> selectedChannels) {
        CategorySelectChannelsDialog dialogFragment = new CategorySelectChannelsDialog();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_SUBSCRIPTIONS_KEY, (ArrayList<? extends Parcelable>) subscriptions);
        args.putParcelableArrayList(ARG_SELECTED_CHANNELS_KEY, (ArrayList<? extends Parcelable>) selectedChannels);
        dialogFragment.setArguments(args);
        return dialogFragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        result = new Bundle();

        if (getArguments() == null) {
            return new MaterialAlertDialogBuilder(requireContext())
                    .setMessage("Error")
                    .create();
        }

        subscriptions = getArguments().getParcelableArrayList(ARG_SUBSCRIPTIONS_KEY);
        if (subscriptions == null || subscriptions.isEmpty()) {
            return new MaterialAlertDialogBuilder(requireContext())
                    .setMessage("You are not subscribed to any channel")
                    .create();
        }

        List<UserSubscription> originalSelectedChannels = getArguments().getParcelableArrayList(ARG_SELECTED_CHANNELS_KEY);
        newSelectedChannels = new ArrayList<>(originalSelectedChannels);
        result.putParcelableArrayList(
                RESULT_KEY,
                (ArrayList<? extends Parcelable>) originalSelectedChannels
        );

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_category_select_channels, null);
        RecyclerView subscriptionsRecyclerView = dialogView.findViewById(R.id.dialog_category_pick_channels_list);
        SubscriptionsAdapter subscriptionsAdapter = new SubscriptionsAdapter(
                subscriptions,
                clickedView -> {
                    if (subscriptions == null || subscriptions.size() == 0)
                        return;
                    UserSubscription selectedChannel = (UserSubscription) clickedView.getTag();
                    CheckBox checkBox = clickedView.findViewById(R.id.list_item_subscription_checkbox);
                    if (newSelectedChannels.contains(selectedChannel)) {
                        checkBox.setChecked(false);
                        newSelectedChannels.remove(selectedChannel);
                    } else {
                        checkBox.setChecked(true);
                        newSelectedChannels.add(selectedChannel);
                    }
                },
                true,
                originalSelectedChannels
        );

        subscriptionsRecyclerView.setAdapter(subscriptionsAdapter);
        subscriptionsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        return new MaterialAlertDialogBuilder(requireContext())
                .setView(dialogView)
                .setPositiveButton(
                        getString(R.string.confirm),
                        (dialog, which) -> result.putParcelableArrayList(
                                RESULT_KEY,
                                (ArrayList<? extends Parcelable>) newSelectedChannels
                        ))
                .setNegativeButton(getString(R.string.cancel), (dialog, which) -> dialog.dismiss())
                .create();
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        getParentFragmentManager().setFragmentResult(REQUEST_KEY, result);
    }

}
