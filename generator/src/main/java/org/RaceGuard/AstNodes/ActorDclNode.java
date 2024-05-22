package org.RaceGuard.AstNodes;
import org.RaceGuard.NodeVisitor;

public class ActorDclNode extends DclNode{
    public ActorDclNode(String id) {
        super(id);
    }
    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visit(this);
    }
}
