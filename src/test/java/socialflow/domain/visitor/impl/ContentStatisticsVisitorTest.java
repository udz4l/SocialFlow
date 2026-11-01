package socialflow.domain.visitor.impl;

import org.junit.jupiter.api.Test;
import socialflow.domain.model.*;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ContentStatisticsVisitorTest {

    @Test
    void shouldCountEveryElementInTheTree() {
        Comment comment = new Comment(
                "comment-1",
                "Bob",
                "Nice post"
        );

        Reaction reaction = new Reaction(
                "Alice",
                ReactionType.LIKE
        );

        LinkMedia link = new LinkMedia(
                "https://example.com",
                "Useful resource"
        );

        Post post = new Post(
                "post-1",
                "Alice",
                "Hello world",
                List.of(comment, reaction, link)
        );

        Group group = new Group(
                "Java Group",
                List.of(post)
        );

        Community community = new Community(
                "AITU Community",
                List.of(group)
        );

        ContentStatisticsVisitor visitor =
                new ContentStatisticsVisitor();

        Map<String, Long> statistics = community.accept(visitor);

        assertEquals(1L, statistics.get("Community"));
        assertEquals(1L, statistics.get("Group"));
        assertEquals(1L, statistics.get("Post"));
        assertEquals(1L, statistics.get("Comment"));
        assertEquals(1L, statistics.get("Reaction"));
        assertEquals(1L, statistics.get("LinkMedia"));
    }
}