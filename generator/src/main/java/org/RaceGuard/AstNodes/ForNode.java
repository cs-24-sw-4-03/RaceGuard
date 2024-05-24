package org.RaceGuard.AstNodes;

import org.RaceGuard.NodeVisitor;

public class ForNode extends IterationNode{
    public ForNode() {
        super();
    }
    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visit(this);
    }
}
