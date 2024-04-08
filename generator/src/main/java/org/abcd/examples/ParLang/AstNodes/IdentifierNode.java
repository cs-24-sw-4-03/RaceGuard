package org.abcd.examples.ParLang.AstNodes;

import org.abcd.examples.ParLang.LanguageType;

public class IdentifierNode extends AstNode{
    private final String name;
    private final LanguageType type;

    public IdentifierNode(String name, LanguageType type){
        this.name=name;
        this.type=type;
    }

    public IdentifierNode(String name){
        this.name=name;
        this.type=null;
    }

    public String getName() {
        return name;
    }

    public LanguageType getType() {
        return type;
    }
}
