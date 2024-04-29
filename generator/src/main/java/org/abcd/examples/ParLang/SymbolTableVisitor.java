package org.abcd.examples.ParLang;

import org.abcd.examples.ParLang.AstNodes.*;
import org.abcd.examples.ParLang.Exceptions.DuplicateScopeException;
import org.abcd.examples.ParLang.Exceptions.DuplicateSymbolException;
import org.abcd.examples.ParLang.Exceptions.ScopeNotFoundException;
import org.abcd.examples.ParLang.Exceptions.SymbolNotFoundException;
import org.abcd.examples.ParLang.symbols.Attributes;
import org.abcd.examples.ParLang.symbols.SymbolTable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class SymbolTableVisitor implements NodeVisitor {
    SymbolTable symbolTable;
    private List<RuntimeException> exceptions = new ArrayList<>();

    public List<RuntimeException> getExceptions() {return this.exceptions;}

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
        if(this.symbolTable.addScope(node.getId())){
            //Visits the children of the node to add the symbols to the symbol table
            this.visitChildren(node);
            //Leaves the scope after visiting the children, as the variables in the Script node are not available outside the Script node
            this.symbolTable.leaveScope();
        }else{
            this.exceptions.add(new DuplicateScopeException("Script: " + node.getId() + " already exists"));
        }
    }

    //Declares a variable in the symbol table if it does not already exist
    @Override
    public void visit(VarDclNode node){
        System.out.println("Symbol: " + node.getId());
        if(node.getParent() instanceof StateNode){
            if(this.symbolTable.lookUpStateSymbol(node.getId()) == null){
                Attributes attributes = new Attributes(node.getType(), "dcl");
                this.symbolTable.insertStateSymbol(node.getId(), attributes);
            }else{
                this.exceptions.add(new DuplicateSymbolException("State symbol: " + node.getId() + " already exists in Actor: " + this.symbolTable.findActorParent(node)));
            }
        }else{
            if(this.symbolTable.lookUpSymbolCurrentScope(node.getId()) == null){
                Attributes attributes = new Attributes(node.getType(), "dcl");
                this.symbolTable.insertSymbol(node.getId(), attributes);
            }else{
                this.exceptions.add(new DuplicateSymbolException("Symbol " + node.getId() + " already exists in current scope"));
            }
        }
        this.visitChildren(node);
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
    public void visit(MethodDclNode node){
        //Checks if the method is a local or on method, and inserts it into the correct list
        if(Objects.equals(node.getMethodType(), "local")){
            System.out.println("Inserting Local Method: " + node.getId());
            Attributes attributes = new Attributes(node.getType(), "local");
            this.symbolTable.insertLocalMethod(node.getId(), attributes);
        }else if(Objects.equals(node.getMethodType(), "on")){
            System.out.println("Inserting On Method: " + node.getId());
            Attributes attributes = new Attributes(node.getType(), "on");
            this.symbolTable.insertOnMethod(node.getId(), attributes);
        }
        //Creates a new scope, as long as there is not already a method in the actor that is named the same
        if(this.symbolTable.addScope(node.getId() + this.symbolTable.findActorParent(node))){
            //Visits the children of the node to add the symbols to the symbol table
            this.visitChildren(node);
            //Leaves the scope after visiting the children, as the variables in the method node are not available outside the method node
            this.symbolTable.leaveScope();
        } else {
            this.exceptions.add(new DuplicateScopeException("Duplicate Method scope: " + node.getId() + " in Actor: " + this.symbolTable.findActorParent(node)));
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
            if(!this.symbolTable.insertParams(paramNode.getName(), attributes)){
                this.exceptions.add(new DuplicateSymbolException("Param: " + paramNode.getName() + " already exists in current scope"));
            }
        }
    }

    @Override
    public void visit(ActorDclNode node) {
        System.out.println("\nActor: " + node.getId());
        if(this.symbolTable.addScope(node.getId())){
            //Visits the children of the node to add the symbols to the symbol table
            this.visitChildren(node);
            //Leaves the scope after visiting the children, as the variables in the Actor node are not available outside the Actor node
            this.symbolTable.leaveScope();
        }else{
            this.exceptions.add(new DuplicateScopeException("Actor: " + node.getId() + " already exists"));
        }
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

    @Override
    public void visit(IdentifierNode node) {
        //Checks whether a symbol is a State, Knows or normal symbol and searches the appropriate list
        if(node.getParent().getParent() instanceof StateNode){
            if(this.symbolTable.lookUpStateSymbol(node.getName()) != null){
                System.out.println("Found State symbol: " + node.getName());
            }else{
                System.out.println("Not found State symbol: " + node.getName());
                this.exceptions.add(new SymbolNotFoundException("State symbol: "  + node.getName() + " not found in Actor: " + this.symbolTable.findActorParent(node)));
            }
        //Ensures that we do not search for IdentifierNodes for method calls
        }else if (!(node.getParent() instanceof MethodCallNode)){
            if(this.symbolTable.lookUpSymbol(node.getName()) != null){
                System.out.println("Found symbol: " + node.getName());
            }else{
                System.out.println("Not found symbol: " + node.getName());
                this.exceptions.add(new SymbolNotFoundException("Symbol: "  + node.getName() + " not found"));
            }
        }
    }

    @Override
    public void visit(StateAccessNode node) {
        if(this.symbolTable.lookUpStateSymbol(node.getAccessIdentifier()) != null){
            System.out.println("Found state symbol: " + node.getAccessIdentifier());
        }else{
            System.out.println("Not found state symbol: " + node.getAccessIdentifier());
            this.exceptions.add(new SymbolNotFoundException("State symbol: "  + node.getAccessIdentifier() + " not found in Actor: " + this.symbolTable.findActorParent(node)));
        }

        this.visitChildren(node);
    }

    @Override
    public void visit(ArrayAccessNode node) {
        if(this.symbolTable.lookUpSymbol(node.getAccessIdentifier()) != null){
            System.out.println("Found symbol: " + node.getAccessIdentifier());
        }else{
            System.out.println("Not found symbol: " + node.getAccessIdentifier());
            this.exceptions.add(new SymbolNotFoundException("Array symbol: "  + node.getAccessIdentifier() + " not found"));
        }
    }

    @Override
    public void visit(KnowsAccessNode node) {
        if(this.symbolTable.lookUpKnowsSymbol(node.getAccessIdentifier()) != null){
            System.out.println("Found Knows symbol: " + node.getAccessIdentifier());
        }else{
            System.out.println("Not found Knows symbol: " + node.getAccessIdentifier());
            this.exceptions.add(new SymbolNotFoundException("Knows symbol: "  + node.getAccessIdentifier() + " not found in Actor: " + this.symbolTable.findActorParent(node)));
        }

        this.visitChildren(node);
    }

    @Override
    public void visit(KnowsNode node) {
        //Adds every child to the Knows symbol list, given there are no duplicates
        for(AstNode child: node.getChildren()){
            IdentifierNode idChildNode = (IdentifierNode)child;
            if (this.symbolTable.lookUpKnowsSymbol(idChildNode.getName()) == null) {
                Attributes attributes = new Attributes(idChildNode.getType(), "dcl");
                this.symbolTable.insertKnowsSymbol(idChildNode.getName(), attributes);
            }else{
                this.exceptions.add(new DuplicateScopeException("Duplicate Knows symbol: " + idChildNode.getName() + " found in Actor: " + this.symbolTable.findActorParent(node)));
            }
        }
    }

    @Override
    public void visit(ScriptMethodNode node) {
        //Checks if the method is a local or on method and adds it to the appropriate list
        if(Objects.equals(node.getMethodType(), "local")){
            System.out.println("Inserting Local Method: " + node.getId());
            Attributes attributes = new Attributes(node.getType(), "local");
            this.symbolTable.insertLocalMethod(node.getId(), attributes);
        }else if(Objects.equals(node.getMethodType(), "on")){
            System.out.println("Inserting On Method: " + node.getId());
            Attributes attributes = new Attributes(node.getType(), "on");
            this.symbolTable.insertOnMethod(node.getId(), attributes);
        }
        //Creates a scope as long as there is not another method with the same name
        if(this.symbolTable.addScope(node.getId() + this.symbolTable.findActorParent(node))){
            //Visits the children of the node to add the symbols to the symbol table
            this.visitChildren(node);
            //Leaves the scope after visiting the children, as the variables in the method node are not available outside the method node
            this.symbolTable.leaveScope();
        } else {
            this.exceptions.add(new DuplicateScopeException("Duplicate Method scope: " + node.getId() + " in Script: " + this.symbolTable.findActorParent(node)));
        }
    }

    @Override
    public void visit(FollowsNode node) {
        IdentifierNode script = (IdentifierNode) node.getChildren().get(0);
        if(this.symbolTable.enterScope(script.getName())){
            this.symbolTable.addActorsFollowingScript(this.symbolTable.findActorParent(node));
            this.symbolTable.leaveScope();
        }else {
            exceptions.add(new ScopeNotFoundException("Script: " + script.getName() + " not found"));
        }
    }

    @Override
    public void visit(SpawnActorNode node) {
        this.symbolTable.addScope(node.getNodeHash());
        this.visitChildren(node);
        this.symbolTable.leaveScope();
    }

    //TODO: Find out if this needs any more implementation
    @Override
    public void visit(SenderNode node) {
        this.visitChildren(node);
    }

    @Override
    public void visit(ExpNode node) {
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
    public void visit(SendMsgNode node) {
        this.visitChildren(node);
    }

    @Override
    public void visit(BoolAndExpNode node) {
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
    public void visit(InitNode node){
        this.visitChildren(node);
    }

    @Override
    public void visit(BodyNode node){
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
    public void visit(StateNode node) {
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
    public void visit(BoolExpNode node) {
        this.visitChildren(node);
    }

    @Override
    public void visit(SelfNode node) {
        this.visitChildren(node);
    }

    @Override
    public void visit(ArithExpNode node) {
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
    public void visit(PrintCallNode node) {
        this.visitChildren(node);
    }
}
