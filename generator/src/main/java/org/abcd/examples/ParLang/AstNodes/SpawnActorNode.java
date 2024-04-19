package org.abcd.examples.ParLang.AstNodes;

import org.abcd.examples.ParLang.NodeVisitor;

public class SpawnActorNode extends AstNode{

    public SpawnActorNode(String ActorType){
        setType(ActorType);
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visit(this);
    }
}
