package org.abcd.examples.ParLang;

import org.abcd.examples.ParLang.AstNodes.AstNode;
import org.abcd.examples.ParLang.symbols.Attributes;
import org.abcd.examples.ParLang.symbols.SymbolTable;

public class SymbolTableVisitor {
    SymbolTable symbolTable;

    public SymbolTableVisitor(SymbolTable symbolTable) {
        this.symbolTable = symbolTable;
    }

    private void declareNode(DclNode node, String type){
        if(symbolTable.lookUpSymbol(node.getId()) == null){
            Attributes attributes = new Attributes(type, "dcl");
            symbolTable.insertSymbol(node.getId(), attributes);
            node.type = attributes.getVariableType();
        }
        //TODO: Find out what should be done, when trying to declare a symbol that already exists. Override?
    }

    public void visitChildren(AstNode node){
        for(AstNode child : node.getChildren()){
            child.accept(this);
        }
    }


}
