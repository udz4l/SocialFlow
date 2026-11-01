package socialflow;

import socialflow.config.SocialNetwork;
import socialflow.domain.model.*;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        SocialNetwork network = new SocialNetwork();

        Post post = new Post(
                "post-1",
                "Alice",
                "Welcome to our programming community!",
                List.of(
                        new Comment(
                                "comment-1",
                                "Bob",
                                "Great post!"
                        )
                )
        );

        network.postService().publishPost(post);

        network.reactionService().addReaction(
                post,
                new Reaction("user-1", ReactionType.LIKE)
        );

        network.reactionService().addReaction(
                post,
                new Reaction("user-2", ReactionType.LOVE)
        );

        System.out.println("\n--- SocialFlow Demo ---");

        System.out.println(
                "Feed posts: "
                        + network.feedListener().getFeed().size()
        );

        System.out.println(
                "Notifications: "
                        + network.notificationListener()
                        .getNotifications().size()
        );

        System.out.println(
                "Likes: "
                        + network.trendingListener()
                        .getCount(ReactionType.LIKE)
        );

        System.out.println(
                "Loves: "
                        + network.trendingListener()
                        .getCount(ReactionType.LOVE)
        );

        System.out.println(
                "Total posts: "
                        + network.analyticsAggregator().getTotalPosts()
        );

        System.out.println(
                "Total reactions: "
                        + network.analyticsAggregator().getTotalReactions()
        );
    }
}
