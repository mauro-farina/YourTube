package it.units.sim.yourtube.model;

import com.google.api.client.util.DateTime;
import com.google.api.services.youtube.model.CommentSnippet;

public class VideoComment {

    private final long likesCount;
    private final String author;
    private final DateTime publishedAt;
    private final String comment;

    public VideoComment(CommentSnippet commentSnippet) {
        this.likesCount = commentSnippet.getLikeCount();
        this.author = commentSnippet.getAuthorDisplayName();
        this.publishedAt = commentSnippet.getPublishedAt();
        this.comment = commentSnippet.getTextOriginal();
    }

    public long getLikesCount() {
        return likesCount;
    }

    public String getAuthor() {
        return author;
    }

    public DateTime getPublishedAt() {
        return publishedAt;
    }

    public String getComment() {
        return comment;
    }
}
