package socialflow.domain.visitor.impl;

import java.util.List;

public record ModerationReport(
        int reviewedItems,
        List<String> flaggedItems
) {
    public ModerationReport {
        flaggedItems = List.copyOf(flaggedItems);
    }

    public int flaggedCount() {
        return flaggedItems.size();
    }
}
