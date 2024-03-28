package org.abcd.examples.ParLang.AST;

import org.abcd.examples.ParLang.AstVisitor;
import org.abcd.examples.ParLang.NodeVisitor;

public class ArithExpression extends Expression{
    public enum Type {
        PLUS("+"),
        MINUS("-"),
        MULTIPLY("*"),
        DIVIDE("/"),
        MODULO("%");
        private final String value;

        Type(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    private final Type type;
    private final Expression left;
    private final Expression right;

    public ArithExpression(Type t, Expression l, Expression r){
        type=t;
        left=l;
        right=r;
    }

    public Type getType() {
        return type;
    }

    public Expression getLeft() {
        return left;
    }

    public Expression getRight() {
        return right;
    }

    @Override
    public  AstNode accept(NodeVisitor visitor){
        return visitor.visit(this);
    }
}
