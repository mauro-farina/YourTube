package it.units.sim.yourtube.video.player;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import it.units.sim.yourtube.R;
import it.units.sim.yourtube.model.VideoComment;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.ViewHolder> {

    private List<VideoComment> commentsList;

    public CommentsAdapter(List<VideoComment> commentsList) {
        this.commentsList = commentsList;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setCommentsList(List<VideoComment> commentsList) {
        this.commentsList = commentsList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.list_item_comment, parent, false);
        return new CommentsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TextView authorTextView = holder.getAuthorTextView();
        TextView commentTextView = holder.getCommentTextView();
        authorTextView.setText(commentsList.get(position).getAuthor());
        commentTextView.setText(commentsList.get(position).getComment());
    }

    @Override
    public int getItemCount() {
        return commentsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView authorTextView;
        private final TextView commentTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            authorTextView = itemView.findViewById(R.id.list_item_comment_author);
            commentTextView = itemView.findViewById(R.id.list_item_comment_text);
        }

        public TextView getAuthorTextView() {
            return authorTextView;
        }

        public TextView getCommentTextView() {
            return commentTextView;
        }
    }
}
