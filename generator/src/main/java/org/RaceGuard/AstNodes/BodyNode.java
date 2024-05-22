package org.RaceGuard.AstNodes;

import org.RaceGuard.NodeVisitor;

public class BodyNode extends AstNode {
    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visit(this);
    }
}
