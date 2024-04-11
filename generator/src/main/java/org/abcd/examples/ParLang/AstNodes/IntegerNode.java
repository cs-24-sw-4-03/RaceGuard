package org.abcd.examples.ParLang.AstNodes;

public class IntegerNode extends LiteralNode<Integer>{
    public IntegerNode(int value){super(value);}
    public IntegerNode(int value, boolean isNegative){
        super(value);
        this.setIsNegated(isNegative);
    }
}
