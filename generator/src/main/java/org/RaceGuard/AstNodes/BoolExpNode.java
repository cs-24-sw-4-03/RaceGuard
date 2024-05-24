package org.RaceGuard.AstNodes;

import org.RaceGuard.NodeVisitor;

public class BoolExpNode extends ExpNode {
    private final String opType = "||";

    public BoolExpNode() {
    }
    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visit(this);
    }
}
