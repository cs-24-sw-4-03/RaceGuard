package org.abcd.examples.ParLang.AstNodes;

import org.abcd.examples.ParLang.NodeVisitor;

public class SpawnActorNode extends AstNode{
    private final String ActorType;

    public SpawnActorNode(String ActorType){
        this.ActorType=ActorType;
    }

    public String getActorType(){
        return this.ActorType;
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visit(this);
    }
}
