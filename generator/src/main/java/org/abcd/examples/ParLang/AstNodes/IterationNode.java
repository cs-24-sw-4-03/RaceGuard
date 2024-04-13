package org.abcd.examples.ParLang.AstNodes;

import org.abcd.examples.ParLang.NodeVisitor;

public abstract class IterationNode extends AstNode{
    public IterationNode() {
    }
    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visit(this);
    }
}
