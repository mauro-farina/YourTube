package it.units.sim.yourtube.category;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import it.units.sim.yourtube.R;
import it.units.sim.yourtube.model.Category;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.ViewHolder> {

    private List<Category> categories;
    private final View.OnClickListener onItemClickListener;
    private final int viewContext;
    public static final int VIEW_CONTEXT_CATEGORIES_LIST = 0;
    public static final int VIEW_CONTEXT_VIDEOS_FILTER = 1;
    private static final int VIEW_TYPE_NORMAL = 0;
    private static final int VIEW_TYPE_EXTRA_PADDING = 1;

    public CategoriesAdapter(List<Category> categories,
                             View.OnClickListener onItemClickListener,
                             int viewContext) {
        this.categories = categories;
        this.onItemClickListener = onItemClickListener;
        this.viewContext = viewContext;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setCategoriesList(List<Category> categories) {
        this.categories = categories;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (viewContext == VIEW_CONTEXT_VIDEOS_FILTER) {
            return VIEW_TYPE_NORMAL;
        } else if (viewContext == VIEW_CONTEXT_CATEGORIES_LIST) {
            if (position == categories.size()-1) {
                return VIEW_TYPE_EXTRA_PADDING;
            } else {
                return VIEW_TYPE_NORMAL;
            }
        } else {
            return -1;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.list_item_category, parent, false);
        if (viewContext == VIEW_CONTEXT_VIDEOS_FILTER) {
            view.findViewById(R.id.list_item_category_dots_menu).setVisibility(View.GONE);
        }
        if (viewContext == VIEW_CONTEXT_CATEGORIES_LIST
            && viewType == VIEW_TYPE_EXTRA_PADDING) {
            view.setPadding(
                    view.getPaddingLeft(),
                    view.getPaddingTop(),
                    view.getPaddingRight(),
                    250
            );
        }
        return new CategoriesAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Category category = categories.get(position);
        if (viewContext == VIEW_CONTEXT_CATEGORIES_LIST) {
            holder.getDotsMenuButton().setTag(category);
            holder.getDotsMenuButton().setOnClickListener(onItemClickListener);
        } else if (viewContext == VIEW_CONTEXT_VIDEOS_FILTER) {
            holder.itemView.setTag(category);
            holder.itemView.setOnClickListener(onItemClickListener);
        }
        TextView categoryNameTextView = holder.getCategoryNameTextView();
        ImageView categoryIconImageView = holder.getCategoryIconImageView();
        categoryNameTextView.setText(category.getName());
        categoryIconImageView.setImageResource(category.getCategoryIcon().getResourceId());
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView categoryNameTextView;
        private final ImageView categoryIconImageView;
        private final Button dotsMenuButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryNameTextView = itemView.findViewById(R.id.list_item_category_name);
            categoryIconImageView = itemView.findViewById(R.id.list_item_category_icon);
            dotsMenuButton = itemView.findViewById(R.id.list_item_category_dots_menu);
        }

        public TextView getCategoryNameTextView() {
            return categoryNameTextView;
        }
        public ImageView getCategoryIconImageView() {
            return categoryIconImageView;
        }
        public Button getDotsMenuButton() {
            return dotsMenuButton;
        }
    }
}
