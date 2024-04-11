package org.abcd.examples.ParLang.AstNodes;

public class UnaryExpNode extends AstNode{
    private boolean isNegated = false;

    public  UnaryExpNode(){
    }

    public void setIsNegated(boolean isNegated){
        this.isNegated = isNegated;
    }

    public boolean isNegative(){
        return this.isNegated;
    }
}
