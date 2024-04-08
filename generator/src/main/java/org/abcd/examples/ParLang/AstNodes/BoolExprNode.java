package org.abcd.examples.ParLang.AstNodes;

public class BoolExprNode extends ExprNode {
    public enum BoolType {
        GREATER(">"),
        GREATER_OR_EQUAL(">="),
        LESS("<"),
        LESS_OR_EQUAL("<="),
        EQUAL("=="),
        NOT_EQUAL("!="),
        LOGIC_NEGATION("!"),
        LOGIC_AND("&&"),
        LOGIC_OR("||");
        private final String value;

        BoolType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
    private final BoolType boolType;

    public BoolExprNode(BoolType t, AstNode l, AstNode r) {
        this.boolType = t;
        this.addChild(l);
        this.addChild(r);
    }

    public BoolType getBoolType() {
        return boolType;
    }
}
