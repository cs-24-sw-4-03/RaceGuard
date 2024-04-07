package org.abcd.examples.ParLang.AstNodes;

import java.util.ArrayList;
import java.util.List;

public abstract class AstNode {

    private List<AstNode> children = new ArrayList<>();
    private String type;

    public List<AstNode> getChildren() {
        return children;
    }

    public void addChild(AstNode n){
        children.add(n);
    }
}
