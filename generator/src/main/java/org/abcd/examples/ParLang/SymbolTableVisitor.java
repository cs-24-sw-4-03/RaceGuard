package org.abcd.examples.ParLang;

import org.abcd.examples.ParLang.AstNodes.*;
import org.abcd.examples.ParLang.symbols.Attributes;
import org.abcd.examples.ParLang.symbols.SymbolTable;

public class SymbolTableVisitor {
    SymbolTable symbolTable;

    public SymbolTableVisitor(SymbolTable symbolTable) {
        this.symbolTable = symbolTable;
    }

    public void visitChildren(AstNode node){
        for(AstNode child : node.getChildren()){
            child.accept(this);
            //TODO: Talk about accept() in AstNode with the visitor group
        }
    }


    private void declareNode(VarDclNode node){
        if(symbolTable.lookUpSymbol(node.getId()) == null){
            Attributes attributes = new Attributes(node.getType().toString(), "dcl");
            symbolTable.insertSymbol(node.getId(), attributes);
        }
        //TODO: Find out what should be done, when trying to declare a symbol that already exists. Override?
    }

    public void visit(VarDclNode node){
        declareNode(node);
    }

    //TODO: Are we going to have an INodeVisitor?
    //TODO: Speak to the others about how we identify nodes from one another
    public void visit(IterationNode node){
        symbolTable.addScope(node.getNodeHash());
        this.visitChildren(node);
        symbolTable.leaveScope();
    }

    public void visit(SelectNode node){
        symbolTable.addScope(node.getNodeHash());
        this.visitChildren(node);
        symbolTable.leaveScope();
    }

    public void visit(MethodDclNode node){
        if(symbolTable.lookUpSymbol(node.getId()) == null){
            Attributes attributes = new Attributes(node.getReturnType(), "method");
            symbolTable.insertSymbol(node.getId(), attributes);

            symbolTable.addScope(node.getNodeHash());
            this.visitChildren(node);
            symbolTable.leaveScope();
        }
    }

    public void visit(ParametersNode node){
        String scopeName = symbolTable.getCurrentScope().getScopeName();

        for(AstNode child: node.getChildren()){
            Attributes attributes = new Attributes(child.type, "param");
            attributes.setScope(scopeName);
            symbolTable.insertParams(child.getId, attributes);
            //TODO: Ask if we can have id (or similar) on ASTNode
        }
    }

    public void visit(MethodCallNode node){
        this.visitChildren(node);
    }

    public void visit(InitNode node){
        this.visitChildren(node);
    }

    public void visit(BodyNode node){
        this.visitChildren(node);
    }

    public void visit(AssignNode node){
        this.visitChildren(node);
    }

    public void visit( node){
        this.visitChildren(node);
    }

}
