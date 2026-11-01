package socialflow.domain.model;

import socialflow.domain.visitor.Visitor;

import java.util.List;
import java.util.Objects;

public record Group(String name, List<Post> posts)
        implements SocialElement {

    public Group {
        Objects.requireNonNull(name);
        posts = List.copyOf(posts);
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.visitGroup(this);
    }

    @Override
    public List<Post> children() {
        return posts;
    }
}
