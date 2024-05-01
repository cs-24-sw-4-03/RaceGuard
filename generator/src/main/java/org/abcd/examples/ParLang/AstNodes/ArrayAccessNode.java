package org.abcd.examples.ParLang.AstNodes;

import org.abcd.examples.ParLang.NodeVisitor;

public class ArrayAccessNode extends AccessNode{
    public ArrayAccessNode(String accessType, String accessIdentifier) {
        super(accessType, accessIdentifier);
    }

    private int bracketCount = 0;

    public int getBracketCount() {
        return bracketCount;
    }

    public void setBracketCount(int bracketCount) {
        this.bracketCount = bracketCount;
    }


    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visit(this);
    }
}
