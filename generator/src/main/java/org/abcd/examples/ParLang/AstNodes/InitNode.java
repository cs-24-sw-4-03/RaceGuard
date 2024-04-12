package org.abcd.examples.ParLang.AstNodes;

import org.abcd.examples.ParLang.NodeVisitor;

public class InitNode extends AstNode {
    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visit(this);
    }
}
