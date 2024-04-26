package org.abcd.examples.ParLang;

import org.abcd.examples.ParLang.AstNodes.*;
import org.abcd.examples.ParLang.symbols.SymbolTable;

import java.util.ArrayList;
import java.util.List;

public class MethodCallVisitor implements NodeVisitor {
    SymbolTable symbolTable;
    private List<RuntimeException> exceptions = new ArrayList<>();

    public List<RuntimeException> getExceptions() {return this.exceptions;}

    public MethodCallVisitor(SymbolTable symbolTable) {
        this.symbolTable = symbolTable;
    }

    @Override
    public void visit(MethodCallNode node) {
        //First we find all the methods that the actor is allowed to call
        ArrayList<String> legalLocalMethods = this.symbolTable.getDeclaredLocalMethods();
        //Then we check if the called method is part of that list
        if (legalLocalMethods.contains(node.getMethodName())) {
            System.out.println("Local method id " + node.getMethodName() + " found");
        }
        else {
            System.out.println("Local method id " + node.getMethodName() + " not found");
        }
    }

    @Override
    public void visit(SendMsgNode node) {
        //First we enter the scope of the Actor we send the message to
        this.symbolTable.enterScope(this.symbolTable.lookUpSymbol(node.getReceiver()).getVariableType());

        //Then we find the list of messages it can receive
        ArrayList<String> legalOnMethods = this.symbolTable.getDeclaredOnMethods();
        //We then check if the message is part of the list of allowed messages
        if (legalOnMethods.contains(node.getMsgName())) {
            System.out.println("On method id " + node.getMsgName() + " found");
        }
        else {
            System.out.println("On method id " + node.getMsgName() + " not found");
        }

        //We then leave the scope, such that we do not mess with our scope stack
        this.symbolTable.leaveScope();
        this.visitChildren(node);
    }


    @Override
    public void visit(FollowsNode node) {
        //A FollowsNode can only have 1 child. This child is allways an IdentifierNode
        IdentifierNode script = (IdentifierNode) node.getChildren().get(0);
        //We get the list of on methods from the Actor we are currently within
        ArrayList<String> legalOnMethodsActor = this.symbolTable.getDeclaredOnMethods();

        //We then enter the scope of the Script the Actor follows
        this.symbolTable.enterScope(script.getName());
        //We find its on methods
        ArrayList<String> legalOnMethodsScript = this.symbolTable.getDeclaredOnMethods();
        //Then we leave the scope, such that we do not mess with the scope stack
        this.symbolTable.leaveScope();

        //We then check if every entry in the Scripts list also is in the Actors list
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
        this.symbolTable.enterScope(node.getId() + this.symbolTable.findActorParent(node));
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
        //The name of the actor the method is declared in is used to differentiate between methods with the same name in different actors
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
