package org.abcd.examples.ParLang.AstNodes;

import org.abcd.examples.ParLang.NodeVisitor;

public abstract class ExprNode extends AstNode {
    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visit(this);
    }

}
