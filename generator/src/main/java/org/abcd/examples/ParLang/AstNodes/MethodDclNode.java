package org.abcd.examples.ParLang.AstNodes;

import org.abcd.examples.ParLang.NodeVisitor;

public class MethodDclNode extends DclNode{

    private String returnType;
    private String methodType;

    public MethodDclNode(String id, String returnType, String methodType) {
        super(id);
        this.returnType=returnType;
        this.methodType=methodType;
    }

    public String getMethodType(){return  methodType;}

    public String getReturnType() {
        return returnType;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visit(this);
    }
}
