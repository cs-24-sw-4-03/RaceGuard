package org.RaceGuard.AstNodes;

import org.RaceGuard.NodeVisitor;

public class BoolAndExpNode extends ExpNode {

    private final String opType = "&&";
    public BoolAndExpNode() {
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visit(this);
    }
}
