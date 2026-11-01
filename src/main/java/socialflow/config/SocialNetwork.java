package socialflow.config;

import socialflow.domain.visitor.impl.ModerationReportVisitor;
import socialflow.observer.EventSubject;
import socialflow.observer.EventType;
import socialflow.observer.SocialEvent;
import socialflow.observer.listeners.*;
import socialflow.observer.service.PostService;
import socialflow.observer.service.ReactionService;

import java.util.Set;

public class SocialNetwork {

    private final EventSubject<SocialEvent> postSubject;
    private final EventSubject<SocialEvent> reactionSubject;

    private final PostService postService;
    private final ReactionService reactionService;

    private final FeedListener feedListener;
    private final NotificationListener notificationListener;
    private final ModerationListener moderationListener;
    private final ModerationResultListener moderationResults;
    private final TrendingListener trendingListener;
    private final AnalyticsAggregator analyticsAggregator;

    public SocialNetwork() {
        postSubject = new EventSubject<>();
        reactionSubject = new EventSubject<>();

        postService = new PostService(postSubject);
        reactionService = new ReactionService(reactionSubject);

        feedListener = new FeedListener();
        notificationListener = new NotificationListener();

        moderationListener = new ModerationListener(
                new ModerationReportVisitor(
                        Set.of("spam", "scam", "toxic")
                )
        );

        moderationResults = new ModerationResultListener();

        moderationListener.subscribe(
                EventType.MODERATION_COMPLETED,
                moderationResults
        );

        trendingListener = new TrendingListener();
        analyticsAggregator = new AnalyticsAggregator();

        postSubject.subscribe(EventType.POST_CREATED, feedListener);
        postSubject.subscribe(EventType.POST_CREATED, notificationListener);
        postSubject.subscribe(EventType.POST_CREATED, moderationListener);
        postSubject.subscribe(EventType.POST_CREATED, analyticsAggregator);

        reactionSubject.subscribe(EventType.REACTION_ADDED, notificationListener);
        reactionSubject.subscribe(EventType.REACTION_ADDED, trendingListener);
        reactionSubject.subscribe(EventType.REACTION_ADDED, analyticsAggregator);
    }

    public PostService postService() {
        return postService;
    }

    public ReactionService reactionService() {
        return reactionService;
    }

    public FeedListener feedListener() {
        return feedListener;
    }

    public NotificationListener notificationListener() {
        return notificationListener;
    }

    public ModerationListener moderationListener() {
        return moderationListener;
    }

    public ModerationResultListener moderationResults() {
        return moderationResults;
    }

    public TrendingListener trendingListener() {
        return trendingListener;
    }

    public AnalyticsAggregator analyticsAggregator() {
        return analyticsAggregator;
    }

    public EventSubject<SocialEvent> postSubject() {
        return postSubject;
    }

    public EventSubject<SocialEvent> reactionSubject() {
        return reactionSubject;
    }
}