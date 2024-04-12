package org.abcd.examples.ParLang.AstNodes;
import org.abcd.examples.ParLang.NodeVisitor;

public class ActorStateNode extends DclNode{

    public ActorStateNode(String id) {
        super(id);
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visit(this);
    }
}
