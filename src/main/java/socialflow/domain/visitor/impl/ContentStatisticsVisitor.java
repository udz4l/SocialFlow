package socialflow.domain.visitor.impl;

import socialflow.domain.model.*;
import socialflow.domain.visitor.Visitor;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ContentStatisticsVisitor implements Visitor<Map<String, Long>> {

    @Override
    public Map<String, Long> visitCommunity(Community community) {
        return count("Community", community.children());
    }

    @Override
    public Map<String, Long> visitGroup(Group group) {
        return count("Group", group.children());
    }

    @Override
    public Map<String, Long> visitPost(Post post) {
        return count("Post", post.children());
    }

    @Override
    public Map<String, Long> visitComment(Comment comment) {
        return count("Comment", comment.children());
    }

    @Override
    public Map<String, Long> visitImageMedia(ImageMedia image) {
        return count("ImageMedia", image.children());
    }

    @Override
    public Map<String, Long> visitVideoMedia(VideoMedia video) {
        return count("VideoMedia", video.children());
    }

    @Override
    public Map<String, Long> visitReaction(Reaction reaction) {
        return count("Reaction", reaction.children());
    }

    private Map<String, Long> count(
            String elementType,
            List<? extends SocialElement> children
    ) {
        Map<String, Long> result = new LinkedHashMap<>();
        result.put(elementType, 1L);

        for (SocialElement child : children) {
            child.accept(this).forEach(
                    (type, amount) -> result.merge(type, amount, Long::sum)
            );
        }

        return Map.copyOf(result);
    }
}