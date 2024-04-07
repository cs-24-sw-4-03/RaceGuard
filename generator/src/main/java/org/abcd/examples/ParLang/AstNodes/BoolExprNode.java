package org.abcd.examples.ParLang.AstNodes;

public class BoolExprNode extends ExprNode {
    private boolean value;

    public BoolExprNode(boolean value) {
        this.value = value;
    }

    public boolean getValue() {
        return value;
    }

    public void setValue(boolean value) {
        this.value = value;
    }
    @Override
    public void accept(AstNodeVisitor visitor) {
        visitor.visit(this);
    }
}
