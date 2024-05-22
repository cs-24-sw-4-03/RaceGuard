package org.RaceGuard.AstNodes;

import org.RaceGuard.NodeVisitor;

public class NegatedBoolNode extends AstNode {
        public NegatedBoolNode() {
            super();
        }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visit(this);
    }
}
