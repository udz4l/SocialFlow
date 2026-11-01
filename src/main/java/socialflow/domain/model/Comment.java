package socialflow.domain.model;

import socialflow.domain.visitor.Visitor;

import java.util.Objects;

public record Comment(String id, String author, String text)
        implements PostContent {

    public Comment {
        Objects.requireNonNull(id);
        Objects.requireNonNull(author);
        Objects.requireNonNull(text);
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.visitComment(this);
    }
}
