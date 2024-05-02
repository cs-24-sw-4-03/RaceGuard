package org.abcd.examples.ParLang.AstNodes;

import org.abcd.examples.ParLang.NodeVisitor;

public class BoolAndExpNode extends ExpNode {

    private final String opType = "&&";
    public BoolAndExpNode() {
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visit(this);
    }

}
