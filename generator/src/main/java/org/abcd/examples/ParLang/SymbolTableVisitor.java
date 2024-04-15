package org.abcd.examples.ParLang;

import org.abcd.examples.ParLang.AstNodes.*;
import org.abcd.examples.ParLang.symbols.Attributes;
import org.abcd.examples.ParLang.symbols.SymbolTable;

//TODO: Find out if scope checking also includes checking for legal calls to other actors
//TODO: Implement ArrayDcl?
public class SymbolTableVisitor implements NodeVisitor {
    SymbolTable symbolTable;

    public SymbolTableVisitor(SymbolTable symbolTable) {
        this.symbolTable = symbolTable;
    }

    @Override
    public void visitChildren(AstNode node){
        for(AstNode child : node.getChildren()){
            child.accept(this);
        }
    }

    @Override
    public void visit(ScriptDclNode node) {
        this.symbolTable.addScope(node.getNodeHash());
        //Visits the children of the node to add the symbols to the symbol table
        this.visitChildren(node);
        //Leaves the scope after visiting the children, as the variables in the Script node are not available outside the Script node
        this.symbolTable.leaveScope();
    }

    //Declares a variable in the symbol table if it does not already exist
    @Override
    public void visit(VarDclNode node){
        System.out.println("Symbol: " + node.getId());
        if(node.getParent() instanceof ActorStateNode){
            if(this.symbolTable.lookUpStateSymbol(node.getId()) == null){
                Attributes attributes = new Attributes(node.getType(), "dcl");
                this.symbolTable.insertStateSymbol(node.getId(), attributes);
            }

        } else if (node.getParent() instanceof KnowsNode) {
            if(this.symbolTable.lookUpKnowsSymbol(node.getId()) == null){
                Attributes attributes = new Attributes(node.getType(), "dcl");
                this.symbolTable.insertKnowsSymbol(node.getId(), attributes);
            }

        }else{
            if(this.symbolTable.lookUpSymbol(node.getId()) == null){
                Attributes attributes = new Attributes(node.getType(), "dcl");
                this.symbolTable.insertSymbol(node.getId(), attributes);
            }
        }
        //TODO: Find out what should be done, when trying to declare a symbol that already exists. Override? Error?
    }

    //Creates a new scope as a while iteration node is a new scope and leaves it after visiting the children
    @Override
    public void visit(WhileNode node) {
        this.symbolTable.addScope(node.getNodeHash());
        //Visits the children of the node to add the symbols to the symbol table
        this.visitChildren(node);
        //Leaves the scope after visiting the children, as the variables in the iteration node are not available outside the iteration node
        this.symbolTable.leaveScope();
    }

    //Creates a new scope as a for iteration node is a new scope and leaves it after visiting the children
    @Override
    public void visit(ForNode node) {
        this.symbolTable.addScope(node.getNodeHash());
        //Visits the children of the node to add the symbols to the symbol table
        this.visitChildren(node);
        //Leaves the scope after visiting the children, as the variables in the iteration node are not available outside the iteration node
        this.symbolTable.leaveScope();
    }


    @Override
    //Creates a new scope as a select node is a new scope and leaves it after visiting the children
    public void visit(SelectionNode node){
        this.symbolTable.addScope(node.getNodeHash());
        //Visits the children of the node to add the symbols to the symbol table
        this.visitChildren(node);
        //Leaves the scope after visiting the children, as the variables in the select node are not available outside the selection node
        this.symbolTable.leaveScope();
    }

    @Override
    //Adds a method to the symbol table if it does not already exist
    public void visit(MethodDclNode node){
        if(this.symbolTable.lookUpSymbol(node.getId()) == null){
            Attributes attributes = new Attributes(node.getReturnType(), "method");
            this.symbolTable.insertSymbol(node.getId(), attributes);

            this.symbolTable.addScope(node.getNodeHash());
            //Visits the children of the node to add the symbols to the symbol table
            this.visitChildren(node);
            //Leaves the scope after visiting the children, as the variables in the method node are not available outside the method node
            this.symbolTable.leaveScope();
        }
    }


    @Override
    //Adds the parameters of a method to the symbol table
    public void visit(ParametersNode node){
        String scopeName = this.symbolTable.getCurrentScope().getScopeName();

        //Iterates through the children of the node and adds them to the symbol table
        for(AstNode child: node.getChildren()){
            IdentifierNode paramNode = (IdentifierNode)child;
            Attributes attributes = new Attributes(paramNode.getType(), "param");
            attributes.setScope(scopeName);
            System.out.println("Param: " + paramNode.getName());
            this.symbolTable.insertParams(paramNode.getName(), attributes);
        }
    }

    @Override
    public void visit(ActorDclNode node) {
        this.symbolTable.addScope(node.getNodeHash());
        //Visits the children of the node to add the symbols to the symbol table
        this.visitChildren(node);
        //Leaves the scope after visiting the children, as the variables in the Actor node are not available outside the Actor node
        this.symbolTable.leaveScope();
    }

    @Override
    public void visit(MainDclNode node) {
        this.symbolTable.addScope(node.getNodeHash());
        //Visits the children of the node to add the symbols to the symbol table
        this.visitChildren(node);
        //Leaves the scope after visiting the children, as the variables in the Main node are not available outside the Main node
        this.symbolTable.leaveScope();
    }

    @Override
    public void visit(SpawnDclNode node) {
        this.symbolTable.addScope(node.getNodeHash());
        //Visits the children of the node to add the symbols to the symbol table
        this.visitChildren(node);
        //Leaves the scope after visiting the children, as the variables in the Spawn node are not available outside the Spawn node
        this.symbolTable.leaveScope();
    }

    //TODO: Set up error handling if symbol not found for normal symbols, State symbols and Knows symbols
    @Override
    public void visit(IdentifierNode node) {
        if(this.symbolTable.lookUpSymbol(node.getName()) != null){
            System.out.println("Found symbol: " + node.getName());
        }else{
            System.out.println("Not found symbol: " + node.getName());
        }
    }

    @Override
    public void visit(StateAccessNode node) {
        if(this.symbolTable.lookUpStateSymbol(node.getAccessIdentifier()) != null){
            System.out.println("Found state symbol: " + node.getAccessIdentifier());
        }else{
            System.out.println("Not found state symbol: " + node.getAccessIdentifier());
        }

        this.visitChildren(node);
    }

    @Override
    public void visit(KnowsAccessNode node) {
        if(this.symbolTable.lookUpKnowsSymbol(node.getAccessIdentifier()) != null){
            System.out.println("Found state symbol: " + node.getAccessIdentifier());
        }else{
            System.out.println("Not found state symbol: " + node.getAccessIdentifier());
        }

        this.visitChildren(node);
    }



    @Override
    public void visit(SendMsgNode node) {
        this.visitChildren(node);
    }

    @Override
    public void visit(SpawnActorNode node) {
        this.visitChildren(node);
    }

    @Override
    public void visit(ExprNode node) {
        this.visitChildren(node);
    }

    @Override
    public void visit(AccessNode node) {
        this.visitChildren(node);
    }

    @Override
    public void visit(ReturnStatementNode node) {
        this.visitChildren(node);
    }

    @Override
    public void visit(MethodCallNode node){
        this.visitChildren(node);
    }

    @Override
    public void visit(LocalMethodBodyNode node) {
        this.visitChildren(node);
    }

    @Override
    public void visit(ArgumentsNode node) {
        this.visitChildren(node);
    }

    @Override
    public void visit(DclNode node) {
        this.visitChildren(node);
    }

    @Override
    public void visit(InitNode node){
        this.visitChildren(node);
    }

    @Override
    public void visit(BodyNode node){
        this.visitChildren(node);
    }

    @Override
    public void visit(ActorIdentifierNode node) {
        this.visitChildren(node);
    }

    @Override
    public void visit(AssignNode node){
        this.visitChildren(node);
    }

    @Override
    public void visit(InitializationNode node) {
        this.visitChildren(node);
    }

    @Override
    public void visit(ListNode node) {
        this.visitChildren(node);
    }

    @Override
    public void visit(ActorStateNode node) {
        this.visitChildren(node);
    }

    @Override
    public void visit(FollowsNode node) {
        this.visitChildren(node);
    }

    @Override
    public void visit(KnowsNode node) {
        this.visitChildren(node);
    }

    @Override
    public void visit(IntegerNode node) {
        this.visitChildren(node);
    }

    @Override
    public void visit(DoubleNode node) {
        this.visitChildren(node);
    }

    @Override
    public void visit(StringNode node) {
        this.visitChildren(node);
    }

    @Override
    public void visit(BoolExprNode node) {
        this.visitChildren(node);
    }

    @Override
    public void visit(ArithExprNode node) {
        this.visitChildren(node);
    }

    @Override
    public void visit(NegatedBoolNode node) {
        this.visitChildren(node);
    }

    @Override
    public void visit(BoolNode node) {
        this.visitChildren(node);
    }

    @Override
    public void visit(CompareExpNode node) {
        this.visitChildren(node);
    }

    @Override
    public void visit(IterationNode node) {
        this.visitChildren(node);
    }

    @Override
    public void visit(ArrayAccessNode node) {
        this.visitChildren(node);
    }

    @Override
    public void visit(ScriptMethodNode node) {
        this.visitChildren(node);
    }

    @Override
    public void visit(PrintCallNode node) {
        this.visitChildren(node);
    }
}
