package org.abcd.examples.ParLang.AstNodes;

public abstract class VarDclNode extends AstNode {
    private String id;

    public VarDclNode(String id){
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
