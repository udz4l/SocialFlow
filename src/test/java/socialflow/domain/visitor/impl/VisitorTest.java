package socialflow.domain.visitor.impl;

import org.junit.jupiter.api.Test;
import socialflow.domain.model.*;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class VisitorTest {

    private Community createTestCommunity() {
        Comment comment1 = new Comment(
                "c1", "Alice", "I love Java"
        );

        Comment comment2 = new Comment(
                "c2", "Bob", "No spam here"
        );

        Post post1 = new Post(
                "p1",
                "Alice",
                "Java patterns are \"useful\"",
                List.of(
                        comment1,
                        new ImageMedia("image.png", "Java diagram"),
                        new VideoMedia("video.mp4", 120),
                        new Reaction("user1", ReactionType.LIKE),
                        new Reaction("user2", ReactionType.LOVE)
                )
        );

        Post post2 = new Post(
                "p2",
                "Bob",
                "Clean architecture",
                List.of(
                        comment2,
                        new Reaction("user3", ReactionType.LIKE)
                )
        );

        Group group = new Group(
                "Programming",
                List.of(post1, post2)
        );

        return new Community(
                "Tech Community",
                List.of(group)
        );
    }

    @Test
    void engagementVisitorCountsCommentsAndReactions() {
        Community community = createTestCommunity();

        EngagementVisitor visitor = new EngagementVisitor();

        long result = community.accept(visitor);

        assertEquals(5L, result);
    }

    @Test
    void moderationVisitorFindsBlockedWords() {
        Community community = createTestCommunity();

        ModerationReportVisitor visitor =
                new ModerationReportVisitor(Set.of("spam", "toxic"));

        ModerationReport report = community.accept(visitor);

        assertEquals(4, report.reviewedItems());
        assertEquals(1, report.flaggedCount());
        assertTrue(report.flaggedItems().getFirst().contains("spam"));
    }

    @Test
    void contentFilterFindsMatchingPostsAndComments() {
        Community community = createTestCommunity();

        ContentFilterVisitor visitor =
                new ContentFilterVisitor("java");

        List<SocialElement> results = community.accept(visitor);

        assertEquals(2, results.size());
        assertTrue(results.stream().anyMatch(
                element -> element instanceof Post
        ));
        assertTrue(results.stream().anyMatch(
                element -> element instanceof Comment
        ));
    }

    @Test
    void jsonExportPreservesTreeStructureAndEscapesQuotes() {
        Community community = createTestCommunity();

        JsonExportVisitor visitor = new JsonExportVisitor();

        String json = community.accept(visitor);

        assertTrue(json.contains("\"type\": \"community\""));
        assertTrue(json.contains("\"type\": \"group\""));
        assertTrue(json.contains("\"type\": \"post\""));
        assertTrue(json.contains("\"type\":\"comment\""));
        assertTrue(json.contains("\"type\":\"image\""));
        assertTrue(json.contains("\"type\":\"video\""));
        assertTrue(json.contains("\"type\":\"reaction\""));

        assertTrue(json.contains("Java patterns are \\\"useful\\\""));

        assertTrue(
                json.indexOf("\"type\": \"community\"")
                        < json.indexOf("\"type\": \"group\"")
        );

        assertTrue(
                json.indexOf("\"type\": \"group\"")
                        < json.indexOf("\"type\": \"post\"")
        );
    }

    @Test
    void visitorsHandleEmptyCommunity() {
        Community community = new Community(
                "Empty Community",
                List.of()
        );

        assertEquals(
                0L,
                community.accept(new EngagementVisitor())
        );

        ModerationReport report = community.accept(
                new ModerationReportVisitor(Set.of("spam"))
        );

        assertEquals(0, report.reviewedItems());
        assertEquals(0, report.flaggedCount());

        assertTrue(
                community.accept(new ContentFilterVisitor("java")).isEmpty()
        );

        String json = community.accept(new JsonExportVisitor());

        assertTrue(json.contains("\"groups\": []"));
    }

    @Test
    void engagementVisitorShouldIgnoreLinkMedia() {
        Post post = new Post(
                "post-1",
                "Alice",
                "Check this link",
                List.of(
                        new LinkMedia(
                                "https://example.com",
                                "Interesting article"
                        ),
                        new Comment("c1", "Bob", "Nice")
                )
        );

        assertEquals(1L, post.accept(new EngagementVisitor()));
    }

    @Test
    void moderationVisitorShouldCheckLinkDescription() {
        Post post = new Post(
                "post-1",
                "Alice",
                "Normal post",
                List.of(
                        new LinkMedia(
                                "https://example.com",
                                "This is spam content"
                        )
                )
        );

        ModerationReport report = post.accept(
                new ModerationReportVisitor(Set.of("spam"))
        );

        assertEquals(2, report.reviewedItems());
        assertEquals(1, report.flaggedCount());
    }

    @Test
    void contentFilterShouldFindLinkMedia() {
        LinkMedia link = new LinkMedia(
                "https://example.com/java",
                "Java programming tutorial"
        );

        Post post = new Post(
                "post-1",
                "Alice",
                "Check this resource",
                List.of(link)
        );

        List<SocialElement> results = post.accept(
                new ContentFilterVisitor("tutorial")
        );

        assertEquals(1, results.size());
        assertEquals(link, results.getFirst());
    }

    @Test
    void jsonExportShouldIncludeLinkMedia() {
        LinkMedia link = new LinkMedia(
                "https://example.com",
                "Useful resource"
        );

        Post post = new Post(
                "post-1",
                "Alice",
                "Check this link",
                List.of(link)
        );

        String json = post.accept(new JsonExportVisitor());

        assertTrue(json.contains("\"type\":\"link\""));
        assertTrue(json.contains("https://example.com"));
        assertTrue(json.contains("Useful resource"));
    }
}
