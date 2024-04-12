package org.abcd.examples.ParLang.AstNodes;

import org.abcd.examples.ParLang.NodeVisitor;

public class MethodCallNode extends AstNode{

    private String methodName;

    public MethodCallNode(String methodName){
        this.methodName = methodName;
    }

    public String getMethodName(){
        return this.methodName;
    }

    @Override
    public String toString(){
        return "MethodCallNode with method name: " + this.methodName;
    }
    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visit(this);
    }
}
