package org.RaceGuard.AstNodes;

import org.RaceGuard.NodeVisitor;

public class KnowsAccessNode extends AccessNode{

    public KnowsAccessNode(String accessType, String accessValue) {
        super(accessType, accessValue);
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visit(this);
    }
}
