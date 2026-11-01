package socialflow.domain.model;

import socialflow.domain.visitor.Visitor;

import java.util.Objects;

public record VideoMedia(String url, int durationSeconds)
        implements PostContent {

    public VideoMedia {
        Objects.requireNonNull(url);

        if (durationSeconds < 0) {
            throw new IllegalArgumentException("Duration cannot be negative");
        }
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.visitVideoMedia(this);
    }
}
