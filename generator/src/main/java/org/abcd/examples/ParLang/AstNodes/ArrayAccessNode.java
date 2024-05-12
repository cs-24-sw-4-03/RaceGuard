package org.abcd.examples.ParLang.AstNodes;

import org.abcd.examples.ParLang.NodeVisitor;

public class ArrayAccessNode extends AccessNode{
    public ArrayAccessNode(String accessType, String accessIdentifier) {
        super(accessType, accessIdentifier);
    }

    private int dimensions;

    public int getDimensions() {
        return this.dimensions;
    }
    public void setDimensions(int dimensions) {
        this.dimensions = dimensions;
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visit(this);
    }
}
