package org.abcd.examples.ParLang.AstNodes;

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
}
