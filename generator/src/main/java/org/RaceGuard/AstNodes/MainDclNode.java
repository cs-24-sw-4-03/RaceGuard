package org.RaceGuard.AstNodes;

import org.RaceGuard.NodeVisitor;

public class MainDclNode extends DclNode{

    public MainDclNode(String id) {
        super(id);
    }
    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visit(this);
    }
}
