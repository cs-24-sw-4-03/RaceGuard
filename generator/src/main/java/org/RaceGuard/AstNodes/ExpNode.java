package org.RaceGuard.AstNodes;

import org.RaceGuard.NodeVisitor;

public abstract class ExpNode extends AstNode {
    private boolean isParenthesized = false;

    public boolean getIsParenthesized() {
        return isParenthesized;
    }

    public void setIsParenthesized(boolean isParenthesized) {
        this.isParenthesized = isParenthesized;
    }
    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visit(this);
    }

}
