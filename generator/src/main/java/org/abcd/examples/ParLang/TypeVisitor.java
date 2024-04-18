package org.abcd.examples.ParLang;

import org.abcd.examples.ParLang.AstNodes.*;
import org.abcd.examples.ParLang.Exceptions.CompareTypeMatchingException;
import org.abcd.examples.ParLang.symbols.SymbolTable;

public class TypeVisitor implements NodeVisitor {
    SymbolTable symbolTable;

    public TypeVisitor(SymbolTable symbolTable) {
        this.symbolTable = symbolTable;
    }

    @Override
    public void visitChildren(AstNode node){
        for (AstNode child : node.getChildren()) {
            child.accept(this);
        }
    }

    @Override
    public void visit(ScriptDclNode node) {
        this.visitChildren(node);
    }

    @Override
    public void visit(ScriptMethodNode node) {
        this.visitChildren(node);
    }

    @Override
    public void visit(SendMsgNode node) {
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
    public void visit(MethodCallNode node) {
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
    public void visit(ActorDclNode node) {
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
    public void visit(MethodDclNode node) {
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
        ArithExprNode leftChild = ((ArithExprNode) node.getChildren().get(0));
        ArithExprNode rightChild = ((ArithExprNode) node.getChildren().get(1));
        if (leftChild.getOpType() == rightChild.getOpType()) {
            node.setType("Bool");
        } else {
            throw new CompareTypeMatchingException("Type mismatch in comparison expression");
        }
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


}
