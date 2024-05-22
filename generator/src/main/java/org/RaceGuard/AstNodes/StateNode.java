package org.RaceGuard.AstNodes;
import org.RaceGuard.NodeVisitor;

public class StateNode extends DclNode{

    public StateNode(String id) {
        super(id);
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visit(this);
    }
}
