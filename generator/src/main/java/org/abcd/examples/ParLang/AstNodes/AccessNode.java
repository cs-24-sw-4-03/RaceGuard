package org.abcd.examples.ParLang.AstNodes;
import org.abcd.examples.ParLang.NodeVisitor;

public abstract class AccessNode extends AstNode{
    private String accessType;
    private String accessIdentifier;
    public AccessNode(String accessType, String accessValue){
        this.accessType = accessType;
        this.accessIdentifier = accessValue;

    }
    public String getAccessType() {
        return accessType;
    }

    public String getAccessIdentifier() {
        return accessIdentifier;
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visit(this);
    }
}
