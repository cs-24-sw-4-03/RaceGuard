package org.abcd.examples.ParLang.AstNodes;

import org.abcd.examples.ParLang.NodeVisitor;

import java.util.ArrayList;
import java.util.List;

public class ParametersNode extends AstNode{
    public int getNumberOfIdentifiers(){
        return this.getChildren().size();
    }
    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visit(this);
    }
}
