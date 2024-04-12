package org.abcd.examples.ParLang.AstNodes;

import org.abcd.examples.ParLang.NodeVisitor;

public class IntegerNode extends LiteralNode<Integer>{
    public IntegerNode(int value){super(value);}
    public IntegerNode(int value, boolean isNegative){
        super(value);
        this.setIsNegated(isNegative);
    }
    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visit(this);
    }
}
