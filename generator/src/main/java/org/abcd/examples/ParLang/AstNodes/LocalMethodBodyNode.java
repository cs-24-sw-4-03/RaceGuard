package org.abcd.examples.ParLang.AstNodes;

import org.abcd.examples.ParLang.NodeVisitor;

public class LocalMethodBodyNode extends AstNode{
    String returnType;
    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visit(this);
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    public String getReturnType() {
        return returnType;
    }
}
