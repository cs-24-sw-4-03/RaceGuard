package org.abcd.examples.ParLang.AstNodes;

public class MethodDclNode extends DclNode{

    private String returnType;

    private String methodType;

    public MethodDclNode(String id, String returnType, String MethodType) {
        super(id);
        this.returnType=returnType;
        this.methodType=methodType;
    }

    public String getReturnType() {
        return returnType;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }
}
