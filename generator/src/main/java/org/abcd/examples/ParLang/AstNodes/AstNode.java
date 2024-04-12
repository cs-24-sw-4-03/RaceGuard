package org.abcd.examples.ParLang.AstNodes;

import org.abcd.examples.ParLang.NodeVisitor;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class AstNode {
    private List<AstNode> children = new ArrayList<>();
    public List<AstNode> getChildren() {
        return children;
    }

    public void addChild(AstNode n){
        children.add(n);
    }

    //Generates a hash code based on the memory address of the object
    //Not guaranteed to be unique, but probability of identical hashcode is low
    //Override in AstNode to ensure uniqueness where necessary
    public String getNodeHash() {
        return String.valueOf(this.hashCode());
    }
    public abstract void accept(NodeVisitor visitor);

}
