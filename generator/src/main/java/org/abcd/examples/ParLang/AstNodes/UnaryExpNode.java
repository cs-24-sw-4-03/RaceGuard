package org.abcd.examples.ParLang.AstNodes;

public class UnaryExpNode extends ExprNode{
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
