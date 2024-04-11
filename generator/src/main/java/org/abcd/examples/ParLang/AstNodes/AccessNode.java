package org.abcd.examples.ParLang.AstNodes;

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
}
