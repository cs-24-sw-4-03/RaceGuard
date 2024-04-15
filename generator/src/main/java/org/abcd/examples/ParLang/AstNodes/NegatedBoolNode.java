package org.abcd.examples.ParLang.AstNodes;

import org.abcd.examples.ParLang.NodeVisitor;

public class NegatedBoolNode extends AstNode {
        public NegatedBoolNode() {
            super();
        }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visit(this);
    }
}
