package org.abcd.examples.ParLang.AstNodes;

import org.abcd.examples.ParLang.NodeVisitor;

public class SenderNode extends AstNode{
    public void accept(NodeVisitor visitor) {
        visitor.visit(this);
    }

    public SenderNode() {
        setType("SenderNode");
    }
}