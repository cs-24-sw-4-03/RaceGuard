package org.abcd.examples.ParLang.AstNodes;

public class DoubleNode extends LiteralNode<Double>{

    private boolean isNegated = false;
    public DoubleNode(double value){
        super(value);
        };

    public DoubleNode(double value, boolean isNegative){
        super(value);
        this.isNegated = isNegative;
    }

    public void setIsNegative(boolean isNegative){
        this.isNegated = isNegative;
    }

    public boolean isNegative(){
        return this.isNegated;
    }
}
