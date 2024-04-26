package org.abcd.examples.ParLang;

import org.abcd.examples.ParLang.AstNodes.*;
import org.abcd.examples.ParLang.symbols.SymbolTable;

import java.util.ArrayList;

public class MethodCallVisitor implements NodeVisitor {
    SymbolTable symbolTable;

    public MethodCallVisitor(SymbolTable symbolTable) {
        this.symbolTable = symbolTable;
    }

    @Override
    public void visit(MethodCallNode node) {
        ArrayList<String> legalLocalMethods = this.symbolTable.getDeclaredLocalMethods();
        if (legalLocalMethods.contains(node.getMethodName())) {
            System.out.println("Local method id " + node.getMethodName() + " found");
        }
        else {
            System.out.println("Local method id " + node.getMethodName() + " not found");
        }
    }

    @Override
    public void visit(SendMsgNode node) {
        System.out.println(this.symbolTable.lookUpSymbol(node.getReceiver()).getVariableType());
        this.symbolTable.enterScope(this.symbolTable.lookUpSymbol(node.getReceiver()).getVariableType());

        ArrayList<String> legalOnMethods = this.symbolTable.getDeclaredOnMethods();
        if (legalOnMethods.contains(node.getMsgName())) {
            System.out.println("On method id " + node.getMsgName() + " found");
        }
        else {
            System.out.println("On method id " + node.getMsgName() + " not found");
        }

        this.symbolTable.leaveScope();
        this.visitChildren(node);
    }


    @Override
    public void visit(FollowsNode node) {
        IdentifierNode script = (IdentifierNode) node.getChildren().get(0);
        this.symbolTable.enterScope(this.symbolTable.findActorParent(node));
        ArrayList<String> legalOnMethodsActor = this.symbolTable.getDeclaredOnMethods();
        this.symbolTable.leaveScope();

        this.symbolTable.enterScope(script.getName());
        ArrayList<String> legalOnMethodsScript = this.symbolTable.getDeclaredOnMethods();
        this.symbolTable.leaveScope();

        for (String onMethod : legalOnMethodsScript) {
            if (legalOnMethodsActor.contains(onMethod)) {
                System.out.println("Actor: " + this.symbolTable.findActorParent(node) + " has on method: " + onMethod + " from Script: " + script.getName());
            } else {
                System.out.println("Actor: " + this.symbolTable.findActorParent(node) +  " does not have on method: " + onMethod + " from Script: " + script.getName());
            }
        }


        this.visitChildren(node);
    }

    @Override
    public void visitChildren(AstNode node){
        for(AstNode child : node.getChildren()){
            child.accept(this);
        }
    }

    @Override
    public void visit(ScriptDclNode node) {
        this.symbolTable.enterScope(node.getId());
        this.visitChildren(node);
        this.symbolTable.leaveScope();
    }

    @Override
    public void visit(ScriptMethodNode node) {
        this.symbolTable.enterScope(node.getId());
        this.visitChildren(node);
        this.symbolTable.leaveScope();
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
    public void visit(SelectionNode node) {
        this.symbolTable.enterScope(node.getNodeHash());
        this.visitChildren(node);
        this.symbolTable.leaveScope();
    }

    @Override
    public void visit(SpawnActorNode node) {
        this.visitChildren(node);
    }

    @Override
    public void visit(ActorDclNode node) {
        this.symbolTable.enterScope(node.getId());
        this.visitChildren(node);
        this.symbolTable.leaveScope();
    }

    @Override
    public void visit(StateNode node) {
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
    public void visit(LocalMethodBodyNode node) {
        this.visitChildren(node);
    }

    @Override
    public void visit(ArgumentsNode node) {
        this.visitChildren(node);
    }

    @Override
    public void visit(VarDclNode node) {
        this.visitChildren(node);
    }

    @Override
    public void visit(IdentifierNode node) {
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
    public void visit(KnowsNode node) {
        this.visitChildren(node);
    }

    @Override
    public void visit(MethodDclNode node) {
        this.symbolTable.enterScope(node.getId() + this.symbolTable.findActorParent(node));
        this.visitChildren(node);
        this.symbolTable.leaveScope();
    }

    @Override
    public void visit(MainDclNode node) {
        this.symbolTable.enterScope(node.getNodeHash());
        this.visitChildren(node);
        this.symbolTable.leaveScope();
    }

    @Override
    public void visit(SpawnDclNode node) {
        this.symbolTable.enterScope(node.getNodeHash());
        this.visitChildren(node);
        this.symbolTable.leaveScope();
    }

    @Override
    public void visit(ExpNode node) {
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
    public void visit(WhileNode node) {
        this.symbolTable.enterScope(node.getNodeHash());
        this.visitChildren(node);
        this.symbolTable.leaveScope();
    }

    @Override
    public void visit(ForNode node) {
        this.symbolTable.enterScope(node.getNodeHash());
        this.visitChildren(node);
        this.symbolTable.leaveScope();
    }

    @Override
    public void visit(AccessNode node) {
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
    public void visit(BoolAndExpNode node) {
        this.visitChildren(node);
    }

}
