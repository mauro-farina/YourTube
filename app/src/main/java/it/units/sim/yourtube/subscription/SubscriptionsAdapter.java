package it.units.sim.yourtube.subscription;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import it.units.sim.yourtube.R;
import it.units.sim.yourtube.model.UserSubscription;

public class SubscriptionsAdapter extends RecyclerView.Adapter<SubscriptionsAdapter.ViewHolder> {

    private List<UserSubscription> subscriptionsList;
    private final View.OnClickListener onItemClickListener;
    private final boolean selectable;
    @NonNull
    private final List<UserSubscription> selectedChannels;

    public SubscriptionsAdapter(List<UserSubscription> subscriptionsList,
                                View.OnClickListener onListItemClickListener) {
        this(subscriptionsList, onListItemClickListener, false, new ArrayList<>());
    }

    public SubscriptionsAdapter(List<UserSubscription> subscriptionsList,
                                View.OnClickListener onListItemClickListener,
                                boolean selectable,
                                @NonNull List<UserSubscription> selectedChannels) {
        this.subscriptionsList = subscriptionsList;
        this.onItemClickListener = onListItemClickListener;
        this.selectable = selectable;
        this.selectedChannels = selectedChannels;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setSubscriptionsList(List<UserSubscription> subscriptionsList) {
        this.subscriptionsList = subscriptionsList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SubscriptionsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.list_item_subscription, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SubscriptionsAdapter.ViewHolder holder, int position) {
        UserSubscription sub = subscriptionsList.get(position);
        if (selectable) {
            CheckBox checkBox = holder.getCheckBox();
            checkBox.setVisibility(View.VISIBLE);
            checkBox.setOnClickListener(onItemClickListener);
            checkBox.setTag(sub);
            if (selectedChannels.contains(sub)) {
                checkBox.setChecked(true);
            }
        } else {
            holder.itemView.setOnClickListener(onItemClickListener);
            holder.itemView.setTag(sub);
        }
        TextView channelNameTextView = holder.getChannelNameTextView();
        ImageView thumbnailImageView = holder.getThumbnailImageView();
        channelNameTextView.setText(sub.getChannelName());
        Picasso
                .get()
                .load(sub.getThumbnailUrl())
                .into(thumbnailImageView);
    }

    @Override
    public int getItemCount() {
        return subscriptionsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView channelNameTextView;
        private final ImageView thumbnailImageView;
        private final CheckBox checkBox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            channelNameTextView = itemView.findViewById(R.id.list_item_subscription_channel_name);
            thumbnailImageView = itemView.findViewById(R.id.list_item_subscription_thumbnail);
            checkBox = itemView.findViewById(R.id.list_item_subscription_checkbox);
        }

        public TextView getChannelNameTextView() {
            return channelNameTextView;
        }
        public ImageView getThumbnailImageView() {
            return thumbnailImageView;
        }
        public CheckBox getCheckBox() {
            return checkBox;
        }

    }

}
