package it.units.sim.yourtube.subscription;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import it.units.sim.yourtube.R;
import it.units.sim.yourtube.model.UserSubscription;

public class SubscriptionsAdapter extends RecyclerView.Adapter<SubscriptionsAdapter.ViewHolder> {


    private List<UserSubscription> subscriptionsList;
    private final View.OnClickListener onItemClickListener;

    public SubscriptionsAdapter(List<UserSubscription> subscriptionsList) {
        this(subscriptionsList, null);
    }

    public SubscriptionsAdapter(List<UserSubscription> subscriptionsList,
                                View.OnClickListener onListItemClickListener) {
        this.subscriptionsList = subscriptionsList;
        this.onItemClickListener = onListItemClickListener;
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
        if (onItemClickListener != null) {
            holder.itemView.setOnClickListener(onItemClickListener);
        }
        TextView channelNameTextView = holder.getChannelNameTextView();
        ImageView thumbnailImageView = holder.getThumbnailImageView();
        channelNameTextView.setText(subscriptionsList.get(position).getChannelName());
        Picasso
                .get()
                .load(subscriptionsList.get(position).getThumbnailUrl())
                .into(thumbnailImageView);
    }

    @Override
    public int getItemCount() {
        return subscriptionsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // will generate a new layout item for each list item
        private final TextView channelNameTextView;
        private final ImageView thumbnailImageView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            channelNameTextView = itemView.findViewById(R.id.list_item_subscription_channel_name);
            thumbnailImageView = itemView.findViewById(R.id.list_item_subscription_thumbnail);
        }

        public TextView getChannelNameTextView() {
            return channelNameTextView;
        }
        public ImageView getThumbnailImageView() {
            return thumbnailImageView;
        }

    }

}
