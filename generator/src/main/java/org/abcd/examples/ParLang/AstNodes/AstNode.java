package org.abcd.examples.ParLang.AstNodes;

import org.abcd.examples.ParLang.NodeVisitor;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class AstNode {
    private List<AstNode> children = new ArrayList<>();
    private AstNode parent;
    private String type;
    int lineNumber;
    int columnNumber;

    public List<AstNode> getChildren() {
        return children;
    }

    public void addChild(AstNode n){
        children.add(n); // add n as child to this AstNode
        n.setParent(this); // set this AstNode as parent to n
    }

    public void setParent(AstNode parent) {
        this.parent = parent;
    }

    public AstNode getParent() {
        return parent;
    }

    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }

    public int getLineNumber() {
        return lineNumber;
    }
    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    public int getColumnNumber() {
        return columnNumber;
    }

    public void setColumnNumber(int columnNumber) {
        this.columnNumber = columnNumber;
    }


    //Generates a hash code based on the memory address of the object
    //Not guaranteed to be unique, but probability of identical hashcode is low
    //Override in AstNode to ensure uniqueness where necessary
    public String getNodeHash() {
        return String.valueOf(this.hashCode());
    }

    public abstract void accept(NodeVisitor visitor);


}
