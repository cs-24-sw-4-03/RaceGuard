package org.abcd.examples.ParLang.AstNodes;

import org.abcd.examples.ParLang.NodeVisitor;

public class IntegerNode extends LiteralNode<Long>{
    public IntegerNode(long value){super(value);}
    public IntegerNode(long value, boolean isNegative){
        super(value);
        this.setIsNegated(isNegative);
    }
    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visit(this);
    }
}
