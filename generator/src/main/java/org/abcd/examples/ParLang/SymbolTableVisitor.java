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

    //Declares a variable in the symbol table if it does not already exist
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
    //Creates a new scope as an iteration node is a new scope and leaves it after visiting the children
    public void visit(IterationNode node){
        symbolTable.addScope(node.getNodeHash());
        //Visits the children of the node to add the symbols to the symbol table
        this.visitChildren(node);
        //Leaves the scope after visiting the children, as the variables in the iteration node are not available outside the iteration node
        symbolTable.leaveScope();
    }

    //Creates a new scope as a select node is a new scope and leaves it after visiting the children
    public void visit(SelectNode node){
        symbolTable.addScope(node.getNodeHash());
        //Visits the children of the node to add the symbols to the symbol table
        this.visitChildren(node);
        //Leaves the scope after visiting the children, as the variables in the select node are not available outside the select node
        symbolTable.leaveScope();
    }
    //Adds a method to the symbol table if it does not already exist
    public void visit(MethodDclNode node){
        if(symbolTable.lookUpSymbol(node.getId()) == null){
            Attributes attributes = new Attributes(node.getReturnType(), "method");
            symbolTable.insertSymbol(node.getId(), attributes);

            symbolTable.addScope(node.getNodeHash());
            //Visits the children of the node to add the symbols to the symbol table
            this.visitChildren(node);
            //Leaves the scope after visiting the children, as the variables in the method node are not available outside the method node
            symbolTable.leaveScope();
        }
    }

    //Adds the parameters of a method to the symbol table
    public void visit(ParametersNode node){
        String scopeName = symbolTable.getCurrentScope().getScopeName();

        //Iterates through the children of the node and adds them to the symbol table
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



}
