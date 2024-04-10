package org.abcd.examples.ParLang.AstNodes;

public class IntegerNode extends LiteralNode<Integer>{

    private boolean isNegative = false;
    public IntegerNode(int value){super(value);}
    public IntegerNode(int value, boolean isNegative){
        super(value);
        this.isNegative = isNegative;
    }

    public void setIsNegative(boolean isNegative){
        this.isNegative = isNegative;
    }

    public boolean isNegative(){
        return this.isNegative;
    }
}
