package org.RaceGuard.AstNodes;

import org.RaceGuard.NodeVisitor;

public class KnowsNode extends DclNode{
    public KnowsNode(String id) {
        super(id);
    }
    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visit(this);
    }
}
