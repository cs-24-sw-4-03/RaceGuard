package org.abcd.examples.ParLang.AstNodes;

public class ScriptMethodNode extends DclNode{

    private final String returnType;
    private final String methodType;

    public ScriptMethodNode(String id, String returnType, String methodType) {
        super(id);
        this.returnType=returnType;
        this.methodType=methodType;
    }

    public String getMethodType(){return  methodType;}

    public String getReturnType() {
        return returnType;
    }
}
