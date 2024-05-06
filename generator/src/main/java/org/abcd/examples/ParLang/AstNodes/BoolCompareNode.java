package org.abcd.examples.ParLang.AstNodes;

import org.abcd.examples.ParLang.NodeVisitor;

public class BoolCompareNode extends AstNode{
    private String opType;

    public BoolCompareNode(String opType) {
        this.opType = opType;
    }
    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visit(this);
    }
}
