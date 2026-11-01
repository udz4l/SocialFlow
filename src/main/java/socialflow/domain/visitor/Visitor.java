package socialflow.domain.visitor;

import socialflow.domain.model.*;

public interface Visitor<R> {

    R visitCommunity(Community community);

    R visitGroup(Group group);

    R visitPost(Post post);

    R visitComment(Comment comment);

    R visitImageMedia(ImageMedia imageMedia);

    R visitVideoMedia(VideoMedia videoMedia);

    R visitReaction(Reaction reaction);
}
