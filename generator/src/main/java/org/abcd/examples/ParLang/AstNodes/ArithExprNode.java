package org.abcd.examples.ParLang.AstNodes;

import org.abcd.examples.ParLang.NodeVisitor;

public class ArithExprNode extends ExprNode {
    public enum OpType {
        PLUS("+"),
        MINUS("-"),
        MULTIPLY("*"),
        DIVIDE("/"),
        MODULO("%"),
        UNKNOWN("unknown");
        private final String value;

        OpType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    private final OpType opType;

    private boolean isNegated = false;

    public ArithExprNode(OpType t, AstNode l, AstNode r){
        opType=t;
        this.addChild(l);
        this.addChild(r);
    }

    public OpType getOpType() {
        return opType;
    }

    public void setIsNegated(boolean isNegative){
        this.isNegated = isNegative;
    }

    public boolean isNegative(){
        return this.isNegated;
    }
    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visit(this);
    }
}
