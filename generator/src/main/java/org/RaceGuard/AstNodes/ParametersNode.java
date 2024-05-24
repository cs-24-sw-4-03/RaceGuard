package org.RaceGuard.AstNodes;

import org.RaceGuard.NodeVisitor;

public class ParametersNode extends AstNode{
    public int getNumberOfIdentifiers(){
        return this.getChildren().size();
    }
    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visit(this);
    }
}
