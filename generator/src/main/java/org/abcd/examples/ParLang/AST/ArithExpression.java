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

    public ArithExpression(Type t, Expression l, Expression r){
        type=t;
        this.addChild(l);
        this.addChild(r);
    }

    public Type getType() {
        return type;
    }

    @Override
    public  AstNode accept(NodeVisitor visitor){
        return visitor.visit(this);
    }
}
