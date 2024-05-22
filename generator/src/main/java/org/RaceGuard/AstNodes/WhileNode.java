package org.RaceGuard.AstNodes;

import org.RaceGuard.NodeVisitor;

public class WhileNode extends IterationNode{
    public WhileNode () {
        super();
    }
    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visit(this);
    }
}
