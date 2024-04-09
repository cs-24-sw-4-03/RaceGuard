package org.abcd.examples.ParLang.AstNodes;

public class BoolExprNode extends ExprNode {

    private final boolean isNegated;

    public BoolExprNode(boolean negation, AstNode l, AstNode r) {
        this.isNegated = negation;
        this.addChild(l);
        this.addChild(r);
    }

    public BoolExprNode(boolean negation, AstNode c) {
        this.isNegated = negation;
        this.addChild(c);
    }
    public boolean getIsNegated() {
        return isNegated;
    }
}
