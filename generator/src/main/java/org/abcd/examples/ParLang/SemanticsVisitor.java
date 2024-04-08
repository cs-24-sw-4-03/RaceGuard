package org.abcd.examples.ParLang;

import org.abcd.examples.ParLang.AstNodes.AstNode;
import org.abcd.examples.ParLang.symbols.SymbolTable;

public class SemanticsVisitor {
    SymbolTable symbolTable;

    public SemanticsVisitor(SymbolTable symbolTable) {
        this.symbolTable = symbolTable;
    }

    public void visitChildren(AstNode node){
        for (AstNode child : node.getChildren()) {
            child.accept(this);
        }
    }


}
