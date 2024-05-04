package org.abcd.examples.ParLang.AstNodes;

import org.abcd.examples.ParLang.NodeVisitor;

public class ReturnStatementNode extends AstNode{

    public AstNode getReturnee(){
        if(this.getChildren().size()>0){
            return this.getChildren().get(0);
        }else{
            return null;
        }

    }
    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visit(this);
    }
}
