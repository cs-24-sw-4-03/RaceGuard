package org.abcd.examples.ParLang.AST;

import org.abcd.examples.ParLang.AstVisitor;
import org.abcd.examples.ParLang.NodeVisitor;

import java.util.ArrayList;
import java.util.List;

public abstract class AstNode {

    private List<AstNode> children = new ArrayList<>();

    public List<AstNode> getChildren() {
        return children;
    }

    public void addChild(AstNode n){
        children.add(n);
    }

    public abstract AstNode accept(NodeVisitor visitor);
}
