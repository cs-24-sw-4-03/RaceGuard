package org.abcd.examples.ParLang.AstNodes;

abstract class LiteralNode<T> extends AstNode {
    private T value;

    private boolean isNegated = false;

    public LiteralNode(T value){
        this.value=value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }

    public void setIsNegated(boolean isNegated){
        this.isNegated = isNegated;
    }

    public boolean isNegated(){
        return this.isNegated;
    }
}
