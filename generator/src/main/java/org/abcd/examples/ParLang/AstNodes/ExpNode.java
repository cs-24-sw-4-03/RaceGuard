package org.abcd.examples.ParLang.AstNodes;

import org.abcd.examples.ParLang.NodeVisitor;

public abstract class ExpNode extends AstNode {

    private boolean isParenthesized = false;
    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visit(this);
    }

    public boolean isParenthesized() {
        return isParenthesized;
    }

    public void setParenthesized(boolean parenthesized) {
        isParenthesized = parenthesized;
    }
}
