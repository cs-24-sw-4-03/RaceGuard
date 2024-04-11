package org.abcd.examples.ParLang.AstNodes;

public class DoubleNode extends LiteralNode<Double>{

    public DoubleNode(double value){
        super(value);
        };

    public DoubleNode(double value, boolean isNegative){
        super(value);
        this.setIsNegated(isNegative);
    }
}
