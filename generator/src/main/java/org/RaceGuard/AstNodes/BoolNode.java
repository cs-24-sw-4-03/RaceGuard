package org.RaceGuard.AstNodes;

import org.RaceGuard.NodeVisitor;

public class BoolNode extends LiteralNode<Boolean>{
    public BoolNode(boolean value){
        super(value);
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visit(this);
    }
}
