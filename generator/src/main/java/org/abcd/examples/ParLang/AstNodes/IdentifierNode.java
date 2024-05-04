package org.abcd.examples.ParLang.AstNodes;

import org.abcd.examples.ParLang.LanguageType;
import org.abcd.examples.ParLang.NodeVisitor;

public class IdentifierNode extends AstNode{
    private final String name;
    private boolean isActor=false;

    public IdentifierNode(String name, String type){
        this.name=name;
        setType(type);
    }


    public IdentifierNode(String name){
        this.name=name;
        setType(null);
    }

    public String getName() {
        return name;
    }
    public boolean getIsActor(){return isActor;}
    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visit(this);
    }
}
