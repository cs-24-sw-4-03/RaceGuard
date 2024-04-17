package org.abcd.examples.ParLang.AstNodes;
import org.abcd.examples.ParLang.NodeVisitor;

public class StateNode extends DclNode{

    public StateNode(String id) {
        super(id);
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visit(this);
    }
}
