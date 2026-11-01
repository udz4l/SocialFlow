package socialflow.domain.model;

import socialflow.domain.visitor.Visitor;

import java.util.Objects;

public record Reaction(String userId, ReactionType type)
        implements PostContent {

    public Reaction {
        Objects.requireNonNull(userId);
        Objects.requireNonNull(type);
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.visitReaction(this);
    }
}
