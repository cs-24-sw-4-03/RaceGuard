package org.RaceGuard.AstNodes;

import org.RaceGuard.NodeVisitor;

public class InitializationNode extends AstNode{
    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visit(this);
    }
}
