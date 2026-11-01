package socialflow.domain.visitor.impl;

import socialflow.domain.model.*;
import socialflow.domain.visitor.Visitor;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ModerationReportVisitor implements Visitor<ModerationReport> {

    private final Set<String> blockedWords;

    public ModerationReportVisitor(Set<String> blockedWords) {
        this.blockedWords = Set.copyOf(blockedWords);
    }

    @Override
    public ModerationReport visitCommunity(Community community) {
        List<ModerationReport> reports = community.groups().stream()
                .map(group -> group.accept(this))
                .toList();

        return combine(reports);
    }

    @Override
    public ModerationReport visitGroup(Group group) {
        List<ModerationReport> reports = group.posts().stream()
                .map(post -> post.accept(this))
                .toList();

        return combine(reports);
    }

    @Override
    public ModerationReport visitPost(Post post) {
        List<ModerationReport> reports = new ArrayList<>();

        reports.add(checkText("Post " + post.id(), post.text()));

        for (PostContent element : post.content()) {
            reports.add(element.accept(this));
        }

        return combine(reports);
    }

    @Override
    public ModerationReport visitComment(Comment comment) {
        return checkText("Comment " + comment.id(), comment.text());
    }

    @Override
    public ModerationReport visitImageMedia(ImageMedia imageMedia) {
        return emptyReport();
    }

    @Override
    public ModerationReport visitVideoMedia(VideoMedia videoMedia) {
        return emptyReport();
    }

    @Override
    public ModerationReport visitReaction(Reaction reaction) {
        return emptyReport();
    }

    private ModerationReport checkText(String label, String text) {
        boolean flagged = blockedWords.stream()
                .anyMatch(word -> text.toLowerCase()
                        .contains(word.toLowerCase()));

        return new ModerationReport(
                1,
                flagged ? List.of(label + ": " + text) : List.of()
        );
    }

    private ModerationReport combine(List<ModerationReport> reports) {
        int reviewed = reports.stream()
                .mapToInt(ModerationReport::reviewedItems)
                .sum();

        List<String> flagged = reports.stream()
                .flatMap(report -> report.flaggedItems().stream())
                .toList();

        return new ModerationReport(reviewed, flagged);
    }

    private ModerationReport emptyReport() {
        return new ModerationReport(0, List.of());
    }
}
