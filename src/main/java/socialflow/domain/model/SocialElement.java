package socialflow.domain.model;

import socialflow.domain.visitor.Visitor;

import java.util.List;

public interface SocialElement {

    <R> R accept(Visitor<R> visitor);

    default List<? extends SocialElement> children() {
        return List.of();
    }
}
