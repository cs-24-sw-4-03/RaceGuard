package org.abcd.examples.ParLang.AST;

abstract class LiteralNode<T> extends Expression {
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
