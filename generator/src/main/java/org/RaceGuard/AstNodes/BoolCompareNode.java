package org.RaceGuard.AstNodes;

import org.RaceGuard.NodeVisitor;

public class BoolCompareNode extends AstNode{
    private String opType;

    public BoolCompareNode(String opType) {
        this.opType = opType;
    }
    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visit(this);
    }
}
