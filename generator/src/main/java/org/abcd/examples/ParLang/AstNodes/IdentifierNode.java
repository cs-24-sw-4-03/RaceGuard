package org.abcd.examples.ParLang.AstNodes;

import org.abcd.examples.ParLang.LanguageType;

public class IdentifierNode extends AstNode{
    private final String name;
    private final String type;

    public IdentifierNode(String name, String type){
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

    public String getType() {
        return type;
    }
}
