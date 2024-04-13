package org.abcd.examples.ParLang.AstNodes;

import org.abcd.examples.ParLang.NodeVisitor;

public class WhileNode extends IterationNode{
    public WhileNode () {
        super();
    }
    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visit(this);
    }
}
