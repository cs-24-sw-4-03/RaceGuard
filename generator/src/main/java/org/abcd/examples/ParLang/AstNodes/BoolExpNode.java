package org.abcd.examples.ParLang.AstNodes;

import org.abcd.examples.ParLang.NodeVisitor;

public class BoolExpNode extends ExpNode {
    private final String opType = "||";

    public BoolExpNode() {
    }
    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visit(this);
    }
}