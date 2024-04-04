package org.abcd.examples.ParLang.AstNodes;

public class ArithExprNode extends ExprNode {
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

    public ArithExprNode(Type t, ExprNode l, ExprNode r){
        type=t;
        this.addChild(l);
        this.addChild(r);
    }

    public Type getType() {
        return type;
    }
}
