package org.RaceGuard.AstNodes;

import org.RaceGuard.NodeVisitor;

public class ScriptMethodNode extends DclNode{
    private final String methodType;

    public ScriptMethodNode(String id, String returnType, String methodType) {
        super(id);
        setType(returnType);
        this.methodType=methodType;
    }

    public String getMethodType(){return  methodType;}

    public void accept(NodeVisitor visitor) {
        visitor.visit(this);
    }
}
