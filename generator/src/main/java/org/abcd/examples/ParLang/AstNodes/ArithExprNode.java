package org.abcd.examples.ParLang.AstNodes;

public class ArithExprNode extends ExprNode {
    public enum OpType {
        PLUS("+"),
        MINUS("-"),
        MULTIPLY("*"),
        DIVIDE("/"),
        MODULO("%");
        private final String value;

        OpType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    private final OpType opType;

    public ArithExprNode(OpType t, AstNode l, AstNode r){
        opType=t;
        this.addChild(l);
        this.addChild(r);
    }

    public OpType getOpType() {
        return opType;
    }
}
