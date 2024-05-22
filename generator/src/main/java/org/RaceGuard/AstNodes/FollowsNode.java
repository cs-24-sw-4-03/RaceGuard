package org.RaceGuard.AstNodes;

import org.RaceGuard.NodeVisitor;

public class FollowsNode extends AstNode{
    @Override public void accept(NodeVisitor visitor) {
        visitor.visit(this);
    }
}
