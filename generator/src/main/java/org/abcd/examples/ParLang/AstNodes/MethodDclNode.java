package org.abcd.examples.ParLang.AstNodes;
import org.abcd.examples.ParLang.NodeVisitor;

public class MethodDclNode extends ScriptMethodNode{
    public MethodDclNode(String id, String returnType, String methodType) {
        super(id, returnType, methodType);
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visit(this);
    }
}
