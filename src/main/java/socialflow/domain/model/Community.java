package socialflow.domain.model;

import socialflow.domain.visitor.Visitor;

import java.util.List;
import java.util.Objects;

public record Community(String name, List<Group> groups)
        implements SocialElement {

    public Community {
        Objects.requireNonNull(name);
        groups = List.copyOf(groups);
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.visitCommunity(this);
    }

    @Override
    public List<Group> children() {
        return groups;
    }
}
