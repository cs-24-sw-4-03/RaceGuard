package org.abcd.examples.ParLang.AST;

import org.abcd.examples.ParLang.NodeVisitor;

public class FuncDclNode extends AstNode {
    private String id;
    private String returnType;

    public FuncDclNode(String id, String returnType){
        this.id=id;
        this.returnType=returnType;
    }

    public String getId(){return id;};
    public void setId(String id) {
        this.id = id;
    }
    public String getReturnType() {
        return returnType;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    public AstNode accept(NodeVisitor visitor) {
        return visitor.visit(this);
    }
}
