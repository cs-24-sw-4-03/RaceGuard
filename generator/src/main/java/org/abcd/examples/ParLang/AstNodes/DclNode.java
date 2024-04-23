package org.abcd.examples.ParLang.AstNodes;

import org.abcd.examples.ParLang.NodeVisitor;

public abstract class DclNode extends AstNode {
    private String id;

    public DclNode(String id){
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
