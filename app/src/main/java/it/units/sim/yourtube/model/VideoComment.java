package it.units.sim.yourtube.model;

import com.google.api.services.youtube.model.CommentSnippet;

public class VideoComment {

    private final String author;
    private final String comment;

    public VideoComment(CommentSnippet commentSnippet) {
        this.author = commentSnippet.getAuthorDisplayName();
        this.comment = commentSnippet.getTextOriginal();
    }

    public String getAuthor() {
        return author;
    }

    public String getComment() {
        return comment;
    }
}
