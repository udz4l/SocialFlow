package socialflow.domain.model;

import socialflow.domain.visitor.Visitor;

import java.util.Objects;

public record ImageMedia(String url, String altText)
        implements PostContent {

    public ImageMedia {
        Objects.requireNonNull(url);
        Objects.requireNonNull(altText);
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.visitImageMedia(this);
    }
}
