package org.abcd.examples.ParLang;

import org.abcd.examples.ParLang.AstNodes.*;
import org.abcd.examples.ParLang.symbols.SymbolTable;

public class FuncVisitor implements NodeVisitor {
    SymbolTable symbolTable;

    public FuncVisitor(SymbolTable symbolTable) {
        this.symbolTable = symbolTable;
    }

    @Override
    public void visitChildren(AstNode node) {
        for(AstNode child : node.getChildren()){
            child.accept(this);
        }
    }

    //TODO: Find a way to differentiate between local and on methods
    //TODO: Find a way to identify a specific Script, Actor, Method or ScriptMethod
    @Override
    public void visit(ScriptMethodNode node) {
        this.symbolTable.declaredMethods.add(node.getId());
        this.visitChildren(node);
    }

    @Override
    public void visit(ScriptDclNode node) {
        this.symbolTable.declaredScripts.add(node.getId());
        this.visitChildren(node);
    }

    @Override
    public void visit(ActorDclNode node) {
        this.symbolTable.declaredActors.add(node.getId());
        this.visitChildren(node);
    }

    //TODO:  Find out if it is a problem that Dcl uses Id and Call uses Name
    @Override
    public void visit(MethodDclNode node){
        symbolTable.declaredMethods.add(node.getId());
        this.visitChildren(node);
    }

    @Override
    public void visit(MethodCallNode node){
        symbolTable.calledMethods.add(node.getMethodName());
        this.visitChildren(node);
    }

    @Override
    public void visit(SendMsgNode node) {
        symbolTable.calledMethods.add(node.getMsgName());
        symbolTable.calledMsgReceiver.add(node.getReceiver());
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
    public void visit(InitNode node) {
        this.visitChildren(node);
    }

    @Override
    public void visit(BodyNode node) {
        this.visitChildren(node);
    }

    @Override
    public void visit(IdentifierNode node) {
        this.visitChildren(node);
    }

    @Override
    public void visit(ActorIdentifierNode node) {
        this.visitChildren(node);
    }

    @Override
    public void visit(ParametersNode node) {
        this.visitChildren(node);
    }

    @Override
    public void visit(ReturnStatementNode node) {
        this.visitChildren(node);
    }

    @Override
    public void visit(SpawnActorNode node) {
        this.visitChildren(node);
    }

    @Override
    public void visit(MainDclNode node) {
        this.visitChildren(node);
    }

    @Override
    public void visit(SpawnDclNode node) {
        this.visitChildren(node);
    }

    @Override
    public void visit(ExprNode node) {
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
    public void visit(WhileNode node) {
        this.visitChildren(node);
    }

    @Override
    public void visit(ForNode node) {
        this.visitChildren(node);
    }

    @Override
    public void visit(AccessNode node) {
        this.visitChildren(node);
    }

    @Override
    public void visit(SelectionNode node) {
        this.visitChildren(node);
    }

    @Override
    public void visit(ArrayAccessNode node) {
        this.visitChildren(node);
    }

    @Override
    public void visit(StateAccessNode node) {
        this.visitChildren(node);
    }

    @Override
    public void visit(KnowsAccessNode node) {
        this.visitChildren(node);
    }

    @Override
    public void visit(PrintCallNode node) {
        this.visitChildren(node);
    }

    @Override
    public void visit(AssignNode node) {
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
    public void visit(VarDclNode node) {
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

}
