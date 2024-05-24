package org.RaceGuard.AstNodes;

import org.RaceGuard.NodeVisitor;

public abstract class IterationNode extends AstNode{
    public IterationNode() {
    }
    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visit(this);
    }
}
