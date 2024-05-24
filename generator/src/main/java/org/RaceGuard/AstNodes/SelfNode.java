package org.RaceGuard.AstNodes;

import org.RaceGuard.NodeVisitor;

public class SelfNode extends AstNode {

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visit(this);
    }
}
