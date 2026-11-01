package socialflow.domain.model;

import socialflow.domain.visitor.Visitor;

import java.util.List;
import java.util.Objects;

public record Post(
        String id,
        String author,
        String text,
        List<PostContent> content
) implements SocialElement {

    public Post {
        Objects.requireNonNull(id);
        Objects.requireNonNull(author);
        Objects.requireNonNull(text);
        content = List.copyOf(content);
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.visitPost(this);
    }

    @Override
    public List<PostContent> children() {
        return content;
    }
}
