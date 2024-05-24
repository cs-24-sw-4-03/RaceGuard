package org.RaceGuard.AstNodes;

import org.RaceGuard.NodeVisitor;

public class SelectionNode extends AstNode{
    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visit(this);
    }
}
