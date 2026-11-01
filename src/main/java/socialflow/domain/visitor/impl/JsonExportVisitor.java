package socialflow.domain.visitor.impl;

import socialflow.domain.model.*;
import socialflow.domain.visitor.Visitor;

import java.util.stream.Collectors;

public class JsonExportVisitor implements Visitor<String> {

    @Override
    public String visitCommunity(Community community) {
        String groups = community.groups().stream()
                .map(group -> group.accept(this))
                .collect(Collectors.joining(","));

        return """
                {
                  "type": "community",
                  "name": "%s",
                  "groups": [%s]
                }
                """.formatted(escape(community.name()), groups).trim();
    }

    @Override
    public String visitGroup(Group group) {
        String posts = group.posts().stream()
                .map(post -> post.accept(this))
                .collect(Collectors.joining(","));

        return """
                {
                  "type": "group",
                  "name": "%s",
                  "posts": [%s]
                }
                """.formatted(escape(group.name()), posts).trim();
    }

    @Override
    public String visitPost(Post post) {
        String content = post.content().stream()
                .map(element -> element.accept(this))
                .collect(Collectors.joining(","));

        return """
                {
                  "type": "post",
                  "id": "%s",
                  "author": "%s",
                  "text": "%s",
                  "content": [%s]
                }
                """.formatted(
                escape(post.id()),
                escape(post.author()),
                escape(post.text()),
                content
        ).trim();
    }

    @Override
    public String visitComment(Comment comment) {
        return """
                {"type":"comment","id":"%s","author":"%s","text":"%s"}
                """.formatted(
                escape(comment.id()),
                escape(comment.author()),
                escape(comment.text())
        ).trim();
    }

    @Override
    public String visitImageMedia(ImageMedia imageMedia) {
        return """
                {"type":"image","url":"%s","altText":"%s"}
                """.formatted(
                escape(imageMedia.url()),
                escape(imageMedia.altText())
        ).trim();
    }

    @Override
    public String visitVideoMedia(VideoMedia videoMedia) {
        return """
                {"type":"video","url":"%s","durationSeconds":%d}
                """.formatted(
                escape(videoMedia.url()),
                videoMedia.durationSeconds()
        ).trim();
    }

    @Override
    public String visitReaction(Reaction reaction) {
        return """
                {"type":"reaction","userId":"%s","reactionType":"%s"}
                """.formatted(
                escape(reaction.userId()),
                reaction.type()
        ).trim();
    }

    @Override
    public String visitLinkMedia(LinkMedia linkMedia) {
        return """
                {"type":"link","url":"%s","description":"%s"}
                """.formatted(
                escape(linkMedia.url()),
                escape(linkMedia.description())
        ).trim();
    }

    private String escape(String value) {
        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}