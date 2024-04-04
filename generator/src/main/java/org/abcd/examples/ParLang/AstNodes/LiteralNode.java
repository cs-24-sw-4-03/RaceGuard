package org.abcd.examples.ParLang.AstNodes;

abstract class LiteralNode<T> extends ExprNode {
    private T value;

    public LiteralNode(T value){
        this.value=value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }
}
