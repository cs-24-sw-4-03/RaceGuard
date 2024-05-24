package org.RaceGuard.AstNodes;

import org.RaceGuard.NodeVisitor;

public class StateAccessNode extends AccessNode{

    public StateAccessNode(String accessType, String accessValue) {
        super(accessType, accessValue);
    }
    public StateAccessNode(String accessType) {
        super(accessType);
    }
    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visit(this);
    }
}
