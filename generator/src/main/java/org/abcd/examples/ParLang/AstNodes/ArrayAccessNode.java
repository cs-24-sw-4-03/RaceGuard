package org.abcd.examples.ParLang.AstNodes;

import org.abcd.examples.ParLang.NodeVisitor;

public class ArrayAccessNode extends AccessNode{
    public ArrayAccessNode(String accessType, String accessIdentifier) {
        super(accessType, accessIdentifier);
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visit(this);
    }
}
