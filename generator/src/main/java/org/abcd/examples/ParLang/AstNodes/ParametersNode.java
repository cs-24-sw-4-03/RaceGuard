package org.abcd.examples.ParLang.AstNodes;

import java.util.ArrayList;
import java.util.List;

public class ParametersNode extends AstNode{
    public int getNumberOfIdentifiers(){
        return this.getChildren().size();
    }
}
