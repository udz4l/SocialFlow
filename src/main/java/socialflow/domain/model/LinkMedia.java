package socialflow.domain.model;

import socialflow.domain.visitor.Visitor;

import java.util.Objects;

public record LinkMedia(String url, String description) implements PostContent {

    public LinkMedia {
        Objects.requireNonNull(url);
        Objects.requireNonNull(description);

        if (url.isBlank()) {
            throw new IllegalArgumentException("URL cannot be blank");
        }
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.visitLinkMedia(this);
    }
}