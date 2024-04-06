package org.abcd.examples.ParLang.AstNodes;

import org.abcd.examples.ParLang.LanguageType;

public class ActorIdentifierNode extends AstNode{
    private final String name;
    private final String actorType;

    public ActorIdentifierNode(String name, String actorType){
        this.name=name;
        this.actorType=actorType;
    }

    public String getName() {
        return name;
    }

    public String getActorType() {
        return actorType;
    }
}
