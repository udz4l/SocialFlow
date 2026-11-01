package socialflow.domain.visitor.impl;

import socialflow.domain.model.*;
import socialflow.domain.visitor.Visitor;

public class EngagementVisitor implements Visitor<Long> {

    @Override
    public Long visitCommunity(Community community) {
        return community.groups().stream()
                .mapToLong(group -> group.accept(this))
                .sum();
    }

    @Override
    public Long visitGroup(Group group) {
        return group.posts().stream()
                .mapToLong(post -> post.accept(this))
                .sum();
    }

    @Override
    public Long visitPost(Post post) {
        return post.content().stream()
                .mapToLong(element -> element.accept(this))
                .sum();
    }

    @Override
    public Long visitComment(Comment comment) {
        return 1L;
    }

    @Override
    public Long visitImageMedia(ImageMedia imageMedia) {
        return 0L;
    }

    @Override
    public Long visitVideoMedia(VideoMedia videoMedia) {
        return 0L;
    }

    @Override
    public Long visitReaction(Reaction reaction) {
        return 1L;
    }

    @Override
    public Long visitLinkMedia(LinkMedia linkMedia) {
        return 0L;
    }
}