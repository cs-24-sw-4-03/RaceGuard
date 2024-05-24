package org.RaceGuard.AstNodes;

import org.RaceGuard.NodeVisitor;

public class ReturnStatementNode extends AstNode{

    //Returns the returnee. By returnee is mend the entity being return. i.e. "return <returnee>;"
    public AstNode getReturnee(){
        if(this.getChildren().size()>0){// The returnee might not exist. If it does, it is the only child of the ReturnStatementNode
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
