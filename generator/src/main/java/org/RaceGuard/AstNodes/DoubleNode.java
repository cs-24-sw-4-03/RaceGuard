package org.RaceGuard.AstNodes;

import org.RaceGuard.NodeVisitor;

public class DoubleNode extends LiteralNode<Double>{

    public DoubleNode(double value){
        super(value);
        };

    public DoubleNode(double value, boolean isNegative){
        super(value);
        this.setIsNegated(isNegative);
    }
    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visit(this);
    }
}
