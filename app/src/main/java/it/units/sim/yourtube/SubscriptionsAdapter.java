package it.units.sim.yourtube;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import it.units.sim.yourtube.model.UserSubscription;

public class SubscriptionsAdapter extends RecyclerView.Adapter<SubscriptionsAdapter.ViewHolder> {


    private List<UserSubscription> subscriptionsList;

    public SubscriptionsAdapter(List<UserSubscription> subscriptionsList) {
        this.subscriptionsList = subscriptionsList;
    }

    public SubscriptionsAdapter() {
        this.subscriptionsList = new ArrayList<>();
    }

    public void setSubscriptionsList(List<UserSubscription> subscriptionsList) {
        this.subscriptionsList = subscriptionsList;
        notifyDataSetChanged();
    }

    /**
     * Called when RecyclerView needs a new {@link ViewHolder} of the given type to represent
     * an item.
     * <p>
     * This new ViewHolder should be constructed with a new View that can represent the items
     * of the given type. You can either create a new View manually or inflate it from an XML
     * layout file.
     * <p>
     * The new ViewHolder will be used to display items of the adapter using
     * {@link #onBindViewHolder(ViewHolder, int, List)}. Since it will be re-used to display
     * different items in the data set, it is a good idea to cache references to sub views of
     * the View to avoid unnecessary {@link View#findViewById(int)} calls.
     *
     * @param parent   The ViewGroup into which the new View will be added after it is bound to
     *                 an adapter position.
     * @param viewType The view type of the new View.
     * @return A new ViewHolder that holds a View of the given view type.
     * @see #getItemViewType(int)
     * @see #onBindViewHolder(ViewHolder, int)
     */
    @NonNull
    @Override
    public SubscriptionsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.list_item_subscription, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    /**
     * Called by RecyclerView to display the data at the specified position. This method should
     * update the contents of the {@link ViewHolder#itemView} to reflect the item at the given
     * position.
     * <p>
     * Note that unlike {@link ListView}, RecyclerView will not call this method
     * again if the position of the item changes in the data set unless the item itself is
     * invalidated or the new position cannot be determined. For this reason, you should only
     * use the <code>position</code> parameter while acquiring the related data item inside
     * this method and should not keep a copy of it. If you need the position of an item later
     * on (e.g. in a click listener), use {@link ViewHolder#getAdapterPosition()} which will
     * have the updated adapter position.
     * <p>
     * Override {@link #onBindViewHolder(ViewHolder, int, List)} instead if Adapter can
     * handle efficient partial bind.
     *
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull SubscriptionsAdapter.ViewHolder holder, int position) {
        TextView channelNameTextView = holder.getChannelNameTextView();
        ImageView thumbnailImageView = holder.getThumbnailImageView();
        channelNameTextView.setText(subscriptionsList.get(position).getChannelName());
        Picasso
                .get()
                .load(subscriptionsList.get(position).getThumbnailUrl())
                .into(thumbnailImageView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
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
