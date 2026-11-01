package socialflow.domain.visitor.impl;

import socialflow.domain.model.*;
import socialflow.domain.visitor.Visitor;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ContentFilterVisitor implements Visitor<List<SocialElement>> {

    private final String keyword;

    public ContentFilterVisitor(String keyword) {
        this.keyword = keyword.toLowerCase(Locale.ROOT);
    }

    @Override
    public List<SocialElement> visitCommunity(Community community) {
        List<SocialElement> results = new ArrayList<>();

        for (Group group : community.groups()) {
            results.addAll(group.accept(this));
        }

        return List.copyOf(results);
    }

    @Override
    public List<SocialElement> visitGroup(Group group) {
        List<SocialElement> results = new ArrayList<>();

        for (Post post : group.posts()) {
            results.addAll(post.accept(this));
        }

        return List.copyOf(results);
    }

    @Override
    public List<SocialElement> visitPost(Post post) {
        List<SocialElement> results = new ArrayList<>();

        if (post.text().toLowerCase(Locale.ROOT).contains(keyword)) {
            results.add(post);
        }

        for (PostContent element : post.content()) {
            results.addAll(element.accept(this));
        }

        return List.copyOf(results);
    }

    @Override
    public List<SocialElement> visitComment(Comment comment) {
        if (comment.text().toLowerCase(Locale.ROOT).contains(keyword)) {
            return List.of(comment);
        }

        return List.of();
    }

    @Override
    public List<SocialElement> visitImageMedia(ImageMedia imageMedia) {
        return List.of();
    }

    @Override
    public List<SocialElement> visitVideoMedia(VideoMedia videoMedia) {
        return List.of();
    }

    @Override
    public List<SocialElement> visitReaction(Reaction reaction) {
        return List.of();
    }
}
