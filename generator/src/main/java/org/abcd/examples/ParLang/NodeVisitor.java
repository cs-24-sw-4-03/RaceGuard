package org.abcd.examples.ParLang;

import org.abcd.examples.ParLang.AstNodes.*;

public interface NodeVisitor {
    void visitChildren(AstNode node);
    void visit(AccessNode node);
    void visit(ActorDclNode node);
    void visit(ActorIdentifierNode node);
    void visit(ActorStateNode node);
    void visit(ArgumentsNode node);
    void visit(ArithExprNode node);
    void visit(AssignNode node);
    void visit(BodyNode node);
    void visit(BoolNode node);
    void visit(CompareExpNode node);
    void visit(DclNode node);
    void visit(DoubleNode node);
    void visit(ExprNode node);
    void visit(FollowsNode node);
    void visit(ForNode node);
    void visit(IdentifierNode node);
    void visit(InitializationNode node);
    void visit(InitNode node);
    void visit(IntegerNode node);
    void visit(IterationNode node);
    void visit(KnowsAccessNode node);
    void visit(KnowsNode node);
    void visit(ListNode node);
    void visit(LocalMethodBodyNode node);
    void visit(MainDclNode node);
    void visit(MethodCallNode node);
    void visit(MethodDclNode node);
    void visit(NegatedBoolNode node);
    void visit(ParametersNode node);
    void visit(PrintCallNode node);
    void visit(ReturnStatementNode node);
    void visit(ScriptDclNode node);
    void visit(ScriptMethodNode node);
    void visit(SelectionNode node);
    void visit(SendMsgNode node);
    void visit(SpawnActorNode node);
    void visit(SpawnDclNode node);
    void visit(StateAccessNode node);
    void visit(StringNode node);
    void visit(UnaryExpNode node);
    void visit(VarDclNode node);
    void visit(WhileNode node);
}
