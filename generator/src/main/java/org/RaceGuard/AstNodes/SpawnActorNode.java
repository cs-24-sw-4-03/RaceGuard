package org.RaceGuard.AstNodes;

import org.RaceGuard.NodeVisitor;

public class SpawnActorNode extends AstNode{

    public SpawnActorNode(String ActorType){
        setType(ActorType);
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visit(this);
    }
}
