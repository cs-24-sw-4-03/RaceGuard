package org.abcd.examples.ParLang.AstNodes;

public class SpawnActorNode extends AstNode{
    private final String ActorType;

    public SpawnActorNode(String ActorType){
        this.ActorType=ActorType;
    }

    public String getActorType(){
        return this.ActorType;
    }
}
