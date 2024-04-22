package org.abcd.examples.ParLang.AstNodes;
import org.abcd.examples.ParLang.NodeVisitor;

public abstract class AccessNode extends AstNode{
    private String accessIdentifier;
    public AccessNode(String accessType, String accessValue){
        setType(accessType); //Type property is inherited from AstNode
        this.accessIdentifier = accessValue;
    }
    public AccessNode(String accessType) {
        setType(accessType);
    }

    public String getAccessIdentifier() {
        return accessIdentifier;
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visit(this);
    }
}
